/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.cec.internal.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.openhab.binding.cec.internal.protocol.config.CecConfig;
import org.openhab.binding.cec.internal.protocol.config.DataTypes;
import org.openhab.binding.cec.internal.protocol.converter.HexToIntegerConverter;
import org.openhab.binding.cec.internal.protocol.datatypes.def.DataTypeDefinition;
import org.openhab.binding.cec.internal.protocol.datatypes.def.MessageType;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

/**
 * @author Andreas Gebauer - Initial contribution
 */
public class CecDatabase {

  private static final Logger LOG = LoggerFactory.getLogger(CecDatabase.class);
  protected CecConfig config;
  protected DataTypes dataTypes;

  public CecDatabase() {
    this.load();
  }

  private void load() {
    dataTypes = loadCommon();

    URL entry = FrameworkUtil.getBundle(CecDatabase.class).getEntry("database/messages.xml");

    if (entry == null) {
      LOG.error("Unable to load CEC message types! File database/messages.xml not found.");
      return;
    }

    XStream xstream = new XStream(new StaxDriver());
    xstream.registerConverter(new HexToIntegerConverter());
    xstream.alias("cec-config", CecConfig.class);
    xstream.processAnnotations(MessageType.class);

    try {
      InputStream x = entry.openStream();
      config = (CecConfig) xstream.fromXML(x);
      if (config == null) {
        return;
      }
    } catch (IOException e) {
      LOG.error("Unable to load CEC message types!", e);
    }
  }

  private DataTypes loadCommon() {
    URL entry = loadResource("database/common.xml");

    if (entry == null) {
      LOG.error("Unable to load CEC data types! File database/common.xml not found.");
      return null;
    }

    XStream xstream = new XStream(new StaxDriver());
    xstream.alias("dataTypes", DataTypes.class);
    xstream.processAnnotations(DataTypeDefinition.class);
    xstream.processAnnotations(DataTypes.class);

    try {
      InputStream x = entry.openStream();
      return (DataTypes) xstream.fromXML(x);
    } catch (IOException e) {
      LOG.error("Unable to load CEC data types!", e);
    }
    return null;
  }

  private URL loadResource(String string) {
    return FrameworkUtil.getBundle(CecDatabase.class).getEntry(string);
  }

  public DataTypes getDataTypes() {
    return dataTypes;
  }

  public MessageType getMessage(int opCode) {
    for (MessageType message : this.config.getMessages()) {
      if (message.getId() != null && message.getId() == opCode) {
        return message;
      }
    }
    return null;
  }

  public DataTypeDefinition getDataType(String type) {
    return this.dataTypes.getByName(type);
  }
}
