/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.cec.internal.handler;

import static org.openhab.binding.cec.CecBindingConstants.POWERSTATUS;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.State;
import org.openhab.binding.cec.internal.CecService;
import org.openhab.binding.cec.internal.device.CecDevice;
import org.openhab.binding.cec.internal.device.DeviceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link CecHandler} is responsible for handling commands, which are sent to one of the
 * channels.
 * 
 * @author Andreas Gebauer - Initial contribution
 */
public class CecHandler extends BaseThingHandler implements PropertyChangeListener {

  private static final Logger LOG = LoggerFactory.getLogger(CecHandler.class);

  private CecService cecService;

  public CecHandler(Thing thing, CecService cecService) {
    super(thing);
    this.cecService = cecService;
  }

  @Override
  public void handleCommand(ChannelUID channelUID, Command command) {
    LOG.debug("Received channel: {}, command: {}", channelUID, command);

    if (channelUID.getId().equals(POWERSTATUS)) {
      if (command instanceof OnOffType) {
        final String send = ((OnOffType) command == OnOffType.ON) ? "on" : "standby";
        this.cecService.writePlain(String.valueOf(send) + " " + getDeviceType().getId());
      }

      // Note: if communication with thing fails for some reason,
      // indicate that by setting the status with detail information
      // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
      // "Could not control device at IP address x.x.x.x");
    }
  }

  @Override
  public void initialize() {
    LOG.debug("Initializing {}", this.getThing());
    // updateStatus(ThingStatus.INITIALIZING);

    if (this.getThing() instanceof Bridge && this.cecService.isInitialized()) {
      updateStatus(ThingStatus.ONLINE);

      this.cecService.getBridge().registerListener(this);
    } else {
      CecDevice device = getDevice();
      if (device != null) {
        device.registerListener(this);
        updateStatus(ThingStatus.ONLINE);
      } else {
        updateStatus(ThingStatus.OFFLINE);
      }
    }
  }

  private CecDevice getDevice() {
    DeviceType deviceType = getDeviceType();
    return cecService.getDevice(deviceType);
  }

  private DeviceType getDeviceType() {
    DeviceType deviceType = DeviceType.valueOf(this.thing.getUID().getId().toUpperCase());
    return deviceType;
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    LOG.trace("PropertyChangeEvent received: {}:{} -> {}", evt.getSource(), evt.getPropertyName(),
        evt.getNewValue());

    Channel channel = getChannel(evt.getPropertyName());
    if (channel != null) {
      ChannelUID channelUid = channel.getUID();
      State state = getState(evt.getPropertyName(), evt.getNewValue());

      super.updateState(channelUid, state);
    }
  }

  private Channel getChannel(String propertyName) {
    return this.getThing().getChannel(propertyName);
  }

  private State getState(String prop, Object value) {
    if ("activeSource".equals(prop)) {
      return new StringType(value.toString());
    } else if ("powerStatus".equals(prop)) {
      return new StringType(value.toString());
    } else if ("power".equals(prop)) {
      return value == Boolean.TRUE ? OnOffType.ON : OnOffType.OFF;
    }
    return null;
  }
}