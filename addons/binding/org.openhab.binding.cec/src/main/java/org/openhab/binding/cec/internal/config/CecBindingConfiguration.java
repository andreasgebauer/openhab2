/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.cec.internal.config;

/**
 * CEC bindng configuration.
 *  
 * @author Andreas Gebauer - Initial contribution
 */
public class CecBindingConfiguration {

  public static final String EXECUTABLE = "executable";
  public static final String DEVICE = "device";
  public static final String REFRESH_INTERVAL = "refreshInterval";

  public String executable = "/usr/local/bin/cec-client";
  public String device;
  public int refreshInterval = 60000;

}
