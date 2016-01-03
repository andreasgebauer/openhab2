/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.cec.internal.protocol.datatypes.def;

import java.util.Collections;
import java.util.List;

import org.openhab.binding.cec.internal.protocol.DataMapping;
import org.openhab.binding.cec.internal.protocol.converter.HexToIntegerConverter;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;

/**
 * A CEC message type.
 *  
 * @author Andreas Gebauer - Initial contribution
 */
@XStreamAlias("message")
public class MessageType {

  @XStreamConverter(HexToIntegerConverter.class)
  @XStreamAsAttribute
  private Integer id;
  
  @XStreamAsAttribute
  private String name;
  
  @XStreamAsAttribute
  private String category;
  
  @XStreamAsAttribute
  private String target;
  
  private String description;

  private List<DataMapping> datas;

  public MessageType(Integer id, String category, String description, List<DataMapping> data) {
    this.id = id;
    this.category = category;
    this.description = description;
    this.datas = data;
  }

  public Integer getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getCategory() {
    return category;
  }

  public String getDescription() {
    return description;
  }

  public String getTarget() {
    return target;
  }

  public List<DataMapping> getData() {
    if (datas != null) {
      return datas;
    }
    return Collections.emptyList();
  }

  @Override
  public String toString() {
    return name;
  }

}
