/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.cec.internal.device;

import org.openhab.binding.cec.internal.protocol.data.Identifiable;

/**
 * @author Andreas Gebauer - Initial contribution
 */
public enum DeviceType implements Identifiable {

  TV(0x00), RECORDING1(0x01), RECORDING2(0x02), TUNER1(0x03), PLAYBACK1(0x04), AUDIO_SYSTEM(
      0x05), TUNER2(0x06), TUNER3(0x07), PLAYBACK2(0x08), PLAYBACK3(0x09), TUNER4(0x0A), PLAYBACK4(
          0x0B), RESERVED_C(0x0C), RESERVED_D(0x0D), RESERVED_E(0x0E), BROADCAST(0x0F),
  //
  ;

  private int value;

  private DeviceType(int value) {
    this.value = value;
  }

  public int getId() {
    return value;
  }

  public static DeviceType valueOf(int value) {
    for (DeviceType status : values()) {
      if (status.value == value) {
        return status;
      }
    }
    return null;
  }

}
