/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.cec.discovery;

import static org.openhab.binding.cec.CecBindingConstants.SUPPORTED_THING_TYPES_UIDS;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService;
import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.openhab.binding.cec.internal.CecService;
import org.openhab.binding.cec.internal.device.CecDevice;
import org.openhab.binding.cec.internal.device.DeviceType;
import org.openhab.binding.cec.internal.protocol.data.FeatureOpcode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Discovery service for CEC things.
 *
 * @author Andreas Gebauer - Initial contribution
 *
 */
public class CecDiscoveryService extends AbstractDiscoveryService {

  private static final Logger logger = LoggerFactory.getLogger(CecDiscoveryService.class);

  private CecService cecService;

  public CecDiscoveryService() throws IllegalArgumentException {
    super(SUPPORTED_THING_TYPES_UIDS, 5000, false);
  }

  @Override
  protected void startScan() {
    logger.debug("Starting Discovery");
    discoverCecDevices();
  }

  @Override
  protected synchronized void stopScan() {
    logger.debug("Stopping Discovery");
    super.stopScan();
  }

  private void discoverCecDevices() {
    // send as recording2
    int sender = 0x02;

    for (int i = 0; i < 16; i++) {
      // request the power status
      String receiver = String.format("%02x", i + (sender << 4));

      this.cecService.write(receiver + ":" + FeatureOpcode.GIVE_DEVICE_VENDOR_ID.toHexString());

      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        logger.error("Error sleeping", e);
      }

      this.cecService.write(receiver + ":" + FeatureOpcode.GIVE_OSD_NAME.toHexString());

      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        logger.error("Error sleeping", e);
      }
    }

    for (Entry<Integer, CecDevice> entry : this.cecService.getDevices().entrySet()) {
      newDevice(entry.getValue());
    }

  }

  public void newDevice(CecDevice device) {
    logger.debug("Discovered device {}", device);

    // uid must not contains dots
    String deviceName = device.getDeviceType().name();

    ThingTypeUID thingType = getThingType(device.getDeviceType());
    if (thingType != null) {
      ThingUID uid = new ThingUID(thingType, deviceName.toLowerCase());

      logger.debug("Creating thing with uid {} for device {}", uid, device);

      Map<String, Object> properties = new HashMap<>(1);
      DiscoveryResult result = DiscoveryResultBuilder.create(uid).withProperties(properties)
          .withLabel("CEC Device (" + device.getOsdName() + ")").build();
      thingDiscovered(result);
    }
  }

  private ThingTypeUID getThingType(DeviceType deviceType) {
    for (ThingTypeUID thingTypeUID : SUPPORTED_THING_TYPES_UIDS) {
      if (deviceType.name().toLowerCase().startsWith(thingTypeUID.getId())) {
        return thingTypeUID;
      }
    }
    return null;
  }

  public void setCecService(CecService cecService) {
    this.cecService = cecService;
  }

}
