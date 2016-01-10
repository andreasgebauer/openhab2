/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.cec.internal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openhab.binding.cec.internal.config.CecBindingConfiguration;
import org.openhab.binding.cec.internal.device.CecBridge;
import org.openhab.binding.cec.internal.device.CecDevice;
import org.openhab.binding.cec.internal.device.DeviceType;
import org.openhab.binding.cec.internal.protocol.CecDatabase;
import org.openhab.binding.cec.internal.protocol.Message;
import org.openhab.binding.cec.internal.protocol.MessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CEC service implementation.
 * 
 * @author Andreas Gebauer - Initial contribution
 */
public class CecServiceImpl implements CecService {

  private static final Pattern TRAFFIC_PATTERN = Pattern
      .compile("TRAFFIC: \\[.*?\\]\t([><]{2}) (.*)");
  private static final Pattern NOTICE_PATTERN = Pattern.compile("NOTICE:  \\[.*?\\]\t(.*)");

  private static final Logger LOG = LoggerFactory.getLogger(CecServiceImpl.class);

  private String device;

  private BufferedWriter writer;
  private Process process;
  private Map<Integer, CecDevice> devices = new HashMap<>();
  private CecBridge bridge = new CecBridge();
  private CecDatabase database = new CecDatabase();
  private MessageFactory messageFactory = new MessageFactory(this.database);

  private CecBindingConfiguration config;

  private Object setupLock = new Object();
  private boolean initialized = false;

  @Override
  public void setup(CecBindingConfiguration config) {

    this.config = config;

    LOG.trace("Executing setup");

    try {
      if (process == null || process.exitValue() != 0) {
        if (process != null) {
          LOG.debug("Process not running. Exit with: {}", process.exitValue());
        } else {
          LOG.debug("Process not running.");
        }

        setupInternal();
      }
    } catch (IllegalThreadStateException e) {
      // ignored
    }
  }

  private void setupInternal() {
    LOG.debug("Setting up cec-client process");

    this.devices.clear();

    List<String> commandWithArgs = new ArrayList<>(Arrays.asList(this.config.executable));
    if (this.config.device != null) {
      commandWithArgs.addAll(Arrays.asList(this.device));
    }
    commandWithArgs.addAll(Arrays.asList("-o", "openHAB", "-d", "31"));

    try {
      final ProcessBuilder processBuilder = new ProcessBuilder(commandWithArgs);

      LOG.debug("Starting process with command '{}'", commandWithArgs);
      this.process = processBuilder.start();

      final OutputStream outputStream = this.process.getOutputStream();
      this.writer = new BufferedWriter(new OutputStreamWriter(outputStream));

      final InputStream is = this.process.getInputStream();
      final BufferedReader inputReader = new BufferedReader(new InputStreamReader(is));
      new Thread("Input read thread") {
        @Override
        public void run() {
          String line = null;
          try {
            while ((line = inputReader.readLine()) != null) {
              try {
                CecServiceImpl.this.processLineInput(line);
              } catch (Exception e) {
                LOG.error("Got error on line '" + line + "'", e);
              }
            }
          } catch (IOException e) {
            LOG.error("Error reading line", e);
          }
          LOG.info("input read thread stopped.");
        }
      }.start();
      final InputStream err = this.process.getErrorStream();
      final BufferedReader errReader = new BufferedReader(new InputStreamReader(err));
      new Thread("Error read thread") {
        @Override
        public void run() {
          String line = null;
          try {
            while ((line = errReader.readLine()) != null) {
              LOG.error("ERR Stream: {}", line);
            }
          } catch (IOException e) {
            LOG.error("Error reading line from error out", e);
          }
          LOG.info("error out read thread stopped.");
        }
      }.start();

      synchronized (setupLock) {
        try {
          if (!this.initialized) {
            setupLock.wait();
          }
        } catch (InterruptedException e) {
          LOG.error("Setup lock wait interruption", e);
        }
      }
    } catch (IOException e) {
      LOG.error("Error setting up cec-client", e);
    }
  }

  public void deactivate() {
    LOG.debug("Deactivating service");
    if (this.process != null) {
      LOG.debug("Destroying process {}", this.process);
      this.process.destroy();
    }
  }

  protected void processLineInput(final String line) {
    Matcher matcher;
    if ((matcher = TRAFFIC_PATTERN.matcher(line)).matches()) {
      // LOG.trace("Got traffic match for: " + line);
      final String direction = matcher.group(1);
      final String value = matcher.group(2);
      final String[] byteStr = value.split(":");
      // sender and receiver are wrapped in the first byte
      Integer sendRecv = Integer.valueOf(byteStr[0], 16);
      // shift 4 bit to the right
      final int sender = sendRecv >> 4;
      // the other 4 bits are masked
      final int receiver = sendRecv &= 0x0F;
      // extract the opCode
      final String opCodeStr = (byteStr.length > 1) ? byteStr[1] : null;
      final Integer opCode = opCodeStr != null ? Integer.parseInt(opCodeStr, 16) : null;

      StringBuilder data = new StringBuilder();
      for (int i = 2; i < byteStr.length; i++) {
        data.append(byteStr[i]);
      }
      String dataString = data.length() > 0 ? data.toString() : null;

      if (direction.equals(">>")) {
        this.incoming(sender, receiver, opCode, dataString);
      } else {
        this.outgoing(sender, receiver, opCode, dataString);
      }
    } else if ((matcher = NOTICE_PATTERN.matcher(line)).matches()) {
      // LOG.trace("Got notice match for: " + line);

      String notice = matcher.group(1);
      if (notice.startsWith("CEC client registered:")) {
        LOG.info("CEC client registered.");
        synchronized (setupLock) {
          setupLock.notifyAll();

          this.initialized = true;
        }
      }
    } else {
      LOG.trace("line: {}", line);
    }
  }

  private void incoming(final int sender, final int receiver, final Integer opCode,
      final String value) {

    CecDevice senderDevice = getDevice(sender);
    CecDevice receiverDevice = getDevice(receiver);

    if (opCode == null) {
      LOG.trace("No opCode. Message [{}] will not be parsed", value);
      return;
    }

    Message message = messageFactory.parse(opCode, value);
    LOG.debug("Traffic IN: {} --> {} op:{} val:{} msg:{}", senderDevice, receiverDevice, opCode,
        value, message);

    DevicePropertiesUpdater.process(this.getBridge(), senderDevice, receiverDevice, message);
  }

  @Override
  public CecBridge getBridge() {
    return this.bridge;
  }

  private CecDevice getDevice(final int id) {
    CecDevice device = this.devices.get(id);
    if (device == null) {
      device = new CecDevice(DeviceType.valueOf(id));

      LOG.debug("Storing device {}: {}", id, device);
      this.devices.put(id, device);
    }
    return device;
  }

  public CecDevice getDevice(DeviceType type) {
    return this.devices.get(type.getId());
  }

  public Map<Integer, CecDevice> getDevices() {
    return devices;
  }

  protected void outgoing(final int sender, final int receiver, final Integer opCode,
      final String dataString) {
    // we are not really interested in outgoing traffic
    LOG.trace("Traffic: OUT: {}>{} op:{} val:{}", sender, receiver, opCode, dataString);
  }

  /**
   * Transmit a command.
   * 
   * @param cmd
   *          the command to send
   */
  public void write(final String cmd) {
    writePlain("tx " + cmd);
  }

  public void writePlain(final String cmd) {
    LOG.debug("Sending {} to cec-client", cmd);
    try {
      this.writer.append(cmd);
      this.writer.flush();
    } catch (IOException e) {
      LOG.error("Error writing to cec-client. Shutting down ...", e);
      this.process.destroy();
      // this.setup();
    }
  }

  @Override
  public boolean isInitialized() {
    return this.initialized;
  }

}