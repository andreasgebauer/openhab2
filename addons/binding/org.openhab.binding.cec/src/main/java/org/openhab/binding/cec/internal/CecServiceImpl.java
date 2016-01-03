/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.cec.internal;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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

import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.types.State;
import org.openhab.binding.cec.internal.config.CecBindingConfiguration;
import org.openhab.binding.cec.internal.device.CecDevice;
import org.openhab.binding.cec.internal.device.DeviceType;
import org.openhab.binding.cec.internal.protocol.CecDatabase;
import org.openhab.binding.cec.internal.protocol.Message;
import org.openhab.binding.cec.internal.protocol.MessageFactory;
import org.openhab.binding.cec.internal.protocol.data.PowerStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andreas Gebauer - Initial contribution
 */
public class CecServiceImpl implements CecService, PropertyChangeListener {

  private static final Pattern TRAFFIC_PATTERN = Pattern
      .compile("TRAFFIC: \\[.*?\\]\t([><]{2}) (.*)");
  private static final Pattern NOTICE_PATTERN = Pattern.compile("NOTICE:  \\[.*?\\]\t(.*)");

  private static final Logger LOG = LoggerFactory.getLogger(CecServiceImpl.class);

  private String device;
  boolean autodetect = true;

  private BufferedWriter writer;
  private Process process;
  private Map<Integer, CecDevice> devices = new HashMap<Integer, CecDevice>();
  private CecDatabase database = new CecDatabase();
  private MessageFactory messageFactory = new MessageFactory(this.database);

  private CecBindingConfiguration config;

  private Object setupLock = new Object();

  @Override
  public void setup(CecBindingConfiguration config) {

    this.config = config;

    CecServiceImpl.LOG.trace("Executing setup");

    try {
      if (process == null || process.exitValue() != 0) {
        if (process != null) {
          LOG.debug("Process not running. Exit with: " + process.exitValue());
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
    this.devices.clear();

    List<String> commandWithArgs = new ArrayList<String>(Arrays.asList(this.config.executable));
    if (!autodetect) {
      commandWithArgs.addAll(Arrays.asList(this.device));
    }
    commandWithArgs.addAll(Arrays.asList("-o", "openHAB", "-d", "31"));

    try {
      final ProcessBuilder processBuilder = new ProcessBuilder(commandWithArgs);

      LOG.debug("Starting process with command '" + commandWithArgs + "'");
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
            LOG.error("Error reading", e);
          }
          LOG.info("err read thread stopped.");
        }
      }.start();

      synchronized (setupLock) {
        try {
          setupLock.wait();
        } catch (InterruptedException e) {
          LOG.error("Setup lock wait interruption", e);
        }
      }
    } catch (IOException e) {
      LOG.error("Error setting up cec-client", e);
    }
  }

  public void deactivate() {
    this.process.destroy();
  }

  protected void processLineInput(final String line) {
    Matcher matcher = TRAFFIC_PATTERN.matcher(line);
    if (matcher.matches()) {
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
        }
      }
    } else {
      LOG.trace("line: " + line);
    }
  }

  private void incoming(final int sender, final int receiver, final Integer opCode,
      final String value) {

    CecDevice senderDevice = getDevice(sender);
    CecDevice receiverDevice = getDevice(receiver);

    LOG.trace("Traffic IN: " + senderDevice + " --> " + receiverDevice + " op:" + opCode + " val:"
        + value);

    if (opCode == null) {
      LOG.trace("No opCode");
      return;
    }

    Message message = messageFactory.parse(opCode, value);
    LOG.debug(senderDevice + " > " + receiverDevice + ": " + message);

    DevicePropertiesUpdater.process(senderDevice, receiverDevice, message);
  }

  private CecDevice getDevice(final int id) {
    CecDevice device = this.devices.get(id);
    if (device == null) {
      device = new CecDevice(DeviceType.valueOf(id));
      device.registerListener(this);

      LOG.debug("Storing device " + id + ": " + device);
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
    CecServiceImpl.LOG
        .trace("Traffic: OUT: " + sender + ">" + receiver + " op:" + opCode + " val:" + dataString);
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
    CecServiceImpl.LOG.debug("Sending " + cmd + " to CEC adapter");
    try {
      this.writer.append(cmd);
      this.writer.flush();
    } catch (IOException e) {
      CecServiceImpl.LOG.error("Error writing to process. Shutting down ...", (Throwable) e);
      this.process.destroy();
      // this.setup();
    }
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    LOG.trace("PropertyChangeEvent received: {}:{} -> {}", evt.getSource(), evt.getPropertyName(),
        evt.getNewValue());

    String property = evt.getPropertyName();
    CecDevice device = (CecDevice) evt.getSource();
    int deviceId = device.getDeviceType().getId();

    Object newValue = evt.getNewValue();

    State state = null;
    if (newValue instanceof PowerStatus) {
      switch ((PowerStatus) newValue) {
      case ON:
        state = OnOffType.ON;
        break;
      default:
        state = OnOffType.OFF;
        break;
      }
    }

    if (state == null) {
      state = new StringType(newValue.toString());
    }

    // final CecBindingConfig state = this.getBinding(sender, "state");
    // if (state != null) {
    // LOG.trace("Got config for" + senderDevice + ": " + state);
    // if (opCode == 0x80 || opCode == 0x82) {
    // this.eventPublisher.postUpdate(state.item, (State) OnOffType.ON);
    // }
    // if (opCode == 0x36) {
    // this.eventPublisher.postUpdate(state.item, (State) OnOffType.OFF);
    // }
    // else if (opCode == 0x90 && value != null && value.length() == 2) {
    // if (value.equals("00")) {
    // this.eventPublisher.postUpdate(state.item, (State) OnOffType.ON);
    // }
    // else {
    // this.eventPublisher.postUpdate(state.item, (State) OnOffType.OFF);
    // }
    // }
    // }
    // final CecBindingConfig route = this.getBinding(sender, "route");
    // if (route != null) {
    // if (message.hasOpcode(0x80)) {
    // message.getPayload("newAddress");
    // final String physical = value.substring(4);
    // this.eventPublisher.postUpdate(route.item, (State) new StringType(physical));
    // }
    // else if (message.hasOpcode(0x82)) {
    // final String physical = value;
    // this.eventPublisher.postUpdate(route.item, (State) new StringType(physical));
    // }
    // }
    // final CecBindingConfig activeSource = this.getBinding(sender, "activeSource");
    // if (activeSource != null && opCode == 0x82) {
    // final String osdName = senderDevice.getOsdName();
    // if (osdName != null) {
    // this.eventPublisher.postUpdate(activeSource.item, (State) new StringType(osdName));
    // }
    // else {
    // this.eventPublisher.postUpdate(activeSource.item, (State) UnDefType.NULL);
    // }
    // }
    // if (opCode != 0x80 && opCode != 0x82 && opCode != 0x84) {
    // if (opCode == 0x87) {
    // }
    // else if (opCode == 0x47) {
    // senderDevice.setOsdName(this.toAsciiString(value));
    // }
    // }
  }

}