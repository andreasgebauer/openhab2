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
public enum Vendor implements Identifiable {

  /**
   * Samsung (0x0000F0)
   */
  SAMSUNG("SAMSUNG", 0x0000F0),

  /**
   * PulseEight (0x001582)
   */
  PULSEEIGHT("PULSEEIGHT", 0x001582),

  /**
   * Google (0x001A11)
   */
  GOOGLE("GOOGLE", 0x001A11);

  private int id;
  private String name;

  private Vendor(final String name, final int id) {
    this.name = name;
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public static Vendor valueOf(final int vendorVal) {
    Vendor[] values;
    for (int length = (values = values()).length, i = 0; i < length; ++i) {
      final Vendor vendor = values[i];
      if (vendor.id == vendorVal) {
        return vendor;
      }
    }
    return null;
  }

  public static int getLength() {
    return 3;
  }
}
