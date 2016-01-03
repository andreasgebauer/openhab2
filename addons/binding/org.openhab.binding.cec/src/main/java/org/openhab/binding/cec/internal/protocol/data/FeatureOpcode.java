/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.cec.internal.protocol.data;

/**
 * Opcode as stated in https://en.wikipedia.org/wiki/Consumer_Electronics_Control#Protocol.
 *
 * @author Andreas Gebauer - Initial contribution
 */
public enum FeatureOpcode implements Identifiable {
  
  /**
   * 0x32
   */
  SET_MENU_LANGUAGE(0x32),
  /**
   * 0x46
   */
  GIVE_OSD_NAME(0x46),
  /**
   * 0x8C
   */
  GIVE_DEVICE_VENDOR_ID(0x8C),
  /**
   * 0xA0
   */
  VENDOR_COMMAND_WITH_ID(0xA0);

  private final int value;

  private FeatureOpcode(int messageId) {
    this.value = messageId;
  }

  public int getId() {
    return value;
  }

  public static FeatureOpcode valueOf(int id) {
    for (FeatureOpcode status : values()) {
      if (status.value == id) {
        return status;
      }
    }
    return null;
  }

  public static int getLength() {
    return 1;
  }

  public String toHexString() {
    return String.format("%02X", this.value);
  }
}
