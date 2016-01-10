/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.cec.internal;

import java.util.Map;

import org.openhab.binding.cec.internal.config.CecBindingConfiguration;
import org.openhab.binding.cec.internal.device.CecBridge;
import org.openhab.binding.cec.internal.device.CecDevice;
import org.openhab.binding.cec.internal.device.DeviceType;

/**
 * @author Andreas Gebauer - Initial contribution
 */
public interface CecService {

  void setup(CecBindingConfiguration config);

  Map<Integer, CecDevice> getDevices();

  CecDevice getDevice(DeviceType type);

  void write(String message);

  void writePlain(String string);

  boolean isInitialized();

  CecBridge getBridge();

}
