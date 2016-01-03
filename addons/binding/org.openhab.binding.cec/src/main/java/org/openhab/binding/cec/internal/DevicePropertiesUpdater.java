/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.cec.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.eclipse.smarthome.core.library.types.StringType;
import org.openhab.binding.cec.internal.device.CecDevice;
import org.openhab.binding.cec.internal.device.DeviceType;
import org.openhab.binding.cec.internal.protocol.Message;
import org.openhab.binding.cec.internal.protocol.Payload;
import org.openhab.binding.cec.internal.protocol.data.PowerStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andreas Gebauer - Initial contribution
 */
public class DevicePropertiesUpdater {

  private static final Logger LOG = LoggerFactory.getLogger(DevicePropertiesUpdater.class);

  static void process(CecDevice senderDevice, CecDevice receiverDevice, Message message) {

    // set the target if defined
    if (message.getType().getTarget() != null) {
      String[] split = message.getType().getTarget().split("\\.");
      String objectName = split[0];
      String property = split[1];
      // handle the system target
      if ("system".equals(objectName)) {

        if ("standBy".equals(property)) {
          senderDevice.setPowerStatus(PowerStatus.STANDBY);
        }
      }
    }

    // each payload can update our device states
    for (Payload payload : message.getPayloads()) {
      if (payload.getTarget() != null) {
        String[] split = payload.getTarget().split("\\.");
        String objectName = split[0];
        String property = split[1];
        if ("sender".equals(objectName)) {
          injectTarget(senderDevice, payload, property);
        } else if ("receiver".equals(objectName)) {
          injectTarget(receiverDevice, payload, property);
        } else if ("system".equals(objectName)) {
          if ("activeSource".equals(property)) {
            if (senderDevice.getDeviceType() == DeviceType.TV) {
              senderDevice.setPowerStatus(PowerStatus.ON);
            }
            StringType state = new StringType(payload.getValue().toString());
            // for (CecBindingConfig binding : getBindings(-1, "activeSource")) {
            // eventPublisher.postUpdate(binding.item, state);
            // }
          } else {
            LOG.warn("No Action found for {} and {}", objectName, property);
            return;
          }
        } else {
          LOG.warn("No Action found for {} and {}", objectName, property);
          return;
        }
      }
    }
  }

  private static void injectTarget(CecDevice device, Payload payload, String fieldName) {
    try {
      String setterName = "set" + fieldName.substring(0, 1).toUpperCase()
          + fieldName.substring(1, fieldName.length());

      boolean setterFound = false;
      Method[] methods = device.getClass().getMethods();
      for (Method setter : methods) {
        if (setter.getName().equals(setterName)) {
          setter.setAccessible(true);
          setter.invoke(device, payload.getValue());
          setterFound = true;
          break;
        }
      }

      if (!setterFound) {
        LOG.warn("Setter not found for " + fieldName + ". Using the field directly.");
        Field declaredField = device.getClass().getDeclaredField(fieldName);
        declaredField.setAccessible(true);
        declaredField.set(device, payload.getValue());
      }
    } catch (ReflectiveOperationException e) {
      LOG.error("Cannot set Field {} on object {}: {}", fieldName, device, e.getMessage());
    }
  }
}
