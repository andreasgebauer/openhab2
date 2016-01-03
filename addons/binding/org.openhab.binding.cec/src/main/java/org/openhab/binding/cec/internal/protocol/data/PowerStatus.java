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
 * Different states of PowerStatus. Reported when 'Report Power Status' command (message id 90) is
 * sent.
 * 
 * @author Andreas Gebauer - Initial contribution
 */
public enum PowerStatus implements Identifiable {

  /**
   * 0x00
   */
  ON(0x00),
  /**
   * 0x01
   */
  STANDBY(0x01),
  /**
   * 0x02
   */
  IN_TRANSITION_STANDBY_TO_ON(0x02),
  /**
   * 0x03
   */
  IN_TRANSITION_ON_TO_STANDBY(0x03);

  private int value;

  private PowerStatus(int value) {
    this.value = value;
  }

  public int getId() {
    return value;
  }

  public static PowerStatus valueOf(int id) {
    for (PowerStatus status : values()) {
      if (status.value == id) {
        return status;
      }
    }
    return null;
  }

  public static int getLength() {
    return 1;
  }

}
