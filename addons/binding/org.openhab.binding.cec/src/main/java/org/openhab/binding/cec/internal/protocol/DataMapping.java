/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.cec.internal.protocol;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * CEC data mapping.
 *  
 * @author Andreas Gebauer - Initial contribution
 */
@XStreamAlias("data")
public class DataMapping {

  @XStreamAsAttribute
  private String name;
  @XStreamAsAttribute
  private Integer length;
  @XStreamAsAttribute
  private String type;
  @XStreamAsAttribute
  private String target;

  public String getName() {
    return name;
  }

  public Integer getLength() {
    return length;
  }

  public String getType() {
    return type;
  }

  public String getTarget() {
    return target;
  }
}
