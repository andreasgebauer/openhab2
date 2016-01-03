/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.cec.internal.protocol;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.openhab.binding.cec.internal.protocol.data.Identifiable;
import org.openhab.binding.cec.internal.protocol.datatypes.def.DataTypeDefinition;
import org.openhab.binding.cec.internal.protocol.datatypes.def.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory for CEC messages.
 *  
 * @author Andreas Gebauer - Initial contribution
 */
public class MessageFactory {

  private static final Logger LOG = LoggerFactory.getLogger(MessageFactory.class);

  public class StringReader {

    private String payload;

    public StringReader(String payload) {
      this.payload = payload;
    }

    public String read(int length) {
      String result = payload.substring(0, length);
      this.payload = payload.substring(length, payload.length());
      return result;
    }

    public int length() {
      return payload.length();
    }

  }

  private CecDatabase database;

  public MessageFactory(CecDatabase database) {
    this.database = database;
  }

  public Message parse(int opCode, String payload) {
    MessageType definition = this.database.getMessage(opCode);

    if (definition == null) {
      throw new IllegalArgumentException(
          "No MessageType defined for opCode 0x" + Integer.toString(opCode, 16));
    }

    Message message = new Message(definition);

    StringReader sr = new StringReader(payload);
    for (DataMapping dataMapping : definition.getData()) {
      DataTypeDefinition dataType = this.database.getDataType(dataMapping.getType());

      Class<?> type = dataType != null ? dataType.getType() : null;
      if (type == null && dataMapping.getType() != null) {
        try {
          type = Class.forName(dataMapping.getType());
        } catch (ClassNotFoundException e) {
          throw new IllegalArgumentException("Class for type " + type + " not found");
        }
      }

      Integer length = dataType != null ? dataType.getLength() : null;
      if (length == null && dataMapping.getType() != null) {
        length = dataMapping.getLength();
      }
      if (length == null) {
        try {
          length = (Integer) type.getMethod("getLength").invoke(type);
        } catch (ReflectiveOperationException e) {
        }
      }

      if (length != null && sr.length() < length) {
        throw new IllegalStateException("Attempt to read more data than available");
      }

      Object value = convert(sr, type, length);

      message.addData(new Payload(dataMapping, value, dataType));
    }
    return message;
  }

  private <T> T convert(StringReader sr, Class<T> type, Integer lengthInBytes) {
    // built-in data types (not explicitly defined in XML)
    if (lengthInBytes == null) {
      lengthInBytes = sr.length() / 2;
    }
    if (Integer.class.equals(type)) {
      return type.cast(Integer.parseInt(sr.read(lengthInBytes * 2), 16));
    } else if (String.class.equals(type)) {
      StringBuilder hldr = new StringBuilder();
      int read = 0;
      while (sr.length() > 0 && read < lengthInBytes) {
        // one character are 2 bytes (ASCII)
        final int c = Integer.valueOf(sr.read(2), 16);
        hldr.append((char) c);
        read++;
      }
      return type.cast(hldr.toString());
    }

    // XML-defined data types
    if (Identifiable.class.isAssignableFrom(type)) {
      try {
        List<Method> methodCandidates = new ArrayList<Method>();
        Method[] methods = type.getMethods();
        for (Method m : methods) {
          if (m.getName().equals("valueOf")) {
            methodCandidates.add(m);
          }
        }

        if (methodCandidates.isEmpty()) {
          LOG.warn("Method 'valueOf' not found");
          return null;
        }

        Collections.sort(methodCandidates, new Comparator<Method>() {

          @Override
          public int compare(Method o1, Method o2) {
            Class<?> parameterType0 = o1.getParameterTypes()[0];
            if (int.class.equals(parameterType0)) {
              return -1;
            } else if (String.class.equals(parameterType0)) {
              return 1;
            }
            return 0;
          }
        });

        Method method = methodCandidates.get(0);
        Object parameter = sr.read(lengthInBytes * 2);
        if (int.class.isAssignableFrom(method.getParameterTypes()[0])) {
          parameter = Integer.parseInt((String) parameter, 16);
        }
        return type.cast(method.invoke(type, parameter));
      } catch (ReflectiveOperationException e) {
      }
    }

    return null;
  }
}
