/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.cec.internal.protocol.datatypes.def;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * A CEC data type.
 *  
 * @author Andreas Gebauer - Initial contribution
 */
@XStreamAlias("dataType")
public class DataTypeDefinition {

  @XStreamAsAttribute
  private String name;
  @XStreamAsAttribute
  private Integer length;
  @XStreamAsAttribute
  private Class<?> type;

  public String getName() {
    return name;
  }

  public Integer getLength() {
    return length;
  }

  public Class<?> getType() {
    return type;
  }

}
