/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.cec.internal.protocol.converter;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;

/**
 * Implements a Hex value to integer converter and vice versa.
 *
 * @author Andreas Gebauer - Initial contribution
 */
public class HexToIntegerConverter extends AbstractSingleValueConverter {

  @SuppressWarnings("rawtypes")
  @Override
  public boolean canConvert(Class type) {
    return type.equals(Integer.class) || type.equals(int.class);
  }

  @Override
  public String toString(Object obj) {
    return "0x" + Integer.toHexString((Integer) obj);
  }

  @Override
  public Object fromString(String value) {
    long lVal;
    if (value.startsWith("0x")) {
      lVal = Long.decode(value);
    } else {
      lVal = Long.parseLong(value, 16);
    }

    return (int) lVal;
  }
}