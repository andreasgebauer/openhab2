/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.cec.internal.protocol.config;

import java.util.List;

import org.openhab.binding.cec.internal.protocol.datatypes.def.DataTypeDefinition;

import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * Holds data type definitions.
 *  
 * @author Andreas Gebauer - Initial contribution
 */
public class DataTypes {

  @XStreamImplicit
  private List<DataTypeDefinition> dataType;

  public DataTypeDefinition getByName(String name) {
    for (DataTypeDefinition dataTypeDefinition : dataType) {
      if (dataTypeDefinition.getName().equals(name)) {
        return dataTypeDefinition;
      }
    }
    return null;
  }
}
