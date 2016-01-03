/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.cec.internal.protocol.config;

import java.util.ArrayList;
import java.util.List;

import org.openhab.binding.cec.internal.protocol.datatypes.def.MessageType;

import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * CEC configuration.
 *  
 * @author Andreas Gebauer - Initial contribution
 */
public class CecConfig {

  @XStreamImplicit
  private List<MessageType> messages = new ArrayList<MessageType>();

  public List<MessageType> getMessages() {
    return messages;
  }

  @Override
  public String toString() {
    return "CecConfig [messages=" + messages + "]";
  }

}
