/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.cec.internal.protocol;

import org.openhab.binding.cec.internal.protocol.datatypes.def.DataTypeDefinition;

/**
 * @author Andreas Gebauer - Initial contribution
 */
public class Payload {

  private Object value;
  private DataTypeDefinition dataType;
  private DataMapping dataMapping;

  public Payload(DataMapping dataMapping, Object value, DataTypeDefinition dataType) {
    super();
    this.dataMapping = dataMapping;
    this.value = value;
    this.dataType = dataType;
  }

  public String getName() {
    return this.dataMapping.getName();
  }

  public Object getValue() {
    return this.value;
  }

  @Override
  public String toString() {
    return this.dataMapping.getName() + ": " + value + "->" + this.dataMapping.getTarget();
  }

  public String getTarget() {
    return dataMapping.getTarget();
  }

}
