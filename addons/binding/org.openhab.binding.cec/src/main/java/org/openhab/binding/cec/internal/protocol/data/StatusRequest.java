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
 * @author Andreas Gebauer - Initial contribution
 */
public enum StatusRequest {

  ON(0x01), OFF(0x02), ONCE(0x03),
  //
  ;

  private final int value;

  private StatusRequest(int value) {
    this.value = value;
  }

  public int getId() {
    return value;
  }

  public static StatusRequest valueOf(int id) {
    for (StatusRequest status : values()) {
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
