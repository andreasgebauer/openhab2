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
public class PhysicalAddress implements Identifiable {

  int first;
  int second;
  int third;
  int fourth;

  public PhysicalAddress(int[] address) {
    if (address.length != 4) {
      throw new IllegalArgumentException("Address must contain four integer values");
    }
    this.first = address[0];
    this.second = address[1];
    this.third = address[2];
    this.fourth = address[3];
  }

  public int getId() {
    return first << (3 * 4) + second << (2 * 4) + third << (1 * 4) + fourth;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + first;
    result = prime * result + fourth;
    result = prime * result + second;
    result = prime * result + third;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    PhysicalAddress other = (PhysicalAddress) obj;
    if (first != other.first)
      return false;
    if (fourth != other.fourth)
      return false;
    if (second != other.second)
      return false;
    if (third != other.third)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return hexString(first) + "." + hexString(second) + "." + hexString(third) + "."
        + hexString(fourth);
  }

  private String hexString(int value) {
    return String.format("%01X", value);
  }

  public String toHexString() {
    return hexString(first) + hexString(second) + " " + hexString(third) + hexString(fourth);
  }

  public static PhysicalAddress valueOf(int value) {
    int[] address = new int[4];
    address[0] = (value & 0xF000) >> (3 * 4);
    address[1] = (value & 0x0F00) >> (2 * 4);
    address[2] = (value & 0x00F0) >> (1 * 4);
    address[3] = (value & 0x000F);
    return new PhysicalAddress(address);
  }

  public static PhysicalAddress valueOf(String value) {
    if (value.charAt(0) == '\'') {
      value = value.substring(1, value.length() - 1);
    }
    String[] split = value.split("\\.");
    int[] address = new int[4];
    address[0] = Integer.valueOf(split[0], 16);
    address[1] = Integer.valueOf(split[1], 16);
    address[2] = Integer.valueOf(split[2], 16);
    address[3] = Integer.valueOf(split[3], 16);
    return new PhysicalAddress(address);
  }

  public static int getLength() {
    return 2;
  }

}
