/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.cec;

import org.eclipse.smarthome.model.script.engine.action.ActionDoc;
import org.openhab.binding.cec.internal.CecService;

/**
 * A CEC action.
 *  
 * @author Andreas Gebauer - Initial contribution
 */
public class CecAction {

  public static CecService cec;

  /**
   * Writes a message to the CEC device. Could be something like '2f:22:22'. The message will be
   * prefixed with 'tx '.
   *
   * @param message
   *          the message
   *
   */
  @ActionDoc(text = "Writes a message to the CEC device.")
  public static void write(String message) {
    if (cec != null) {
      cec.write(message);
    }
  }

  /**
   * Writes am play message to the CEC device. Could be something like 'tx 2f:22:22'.
   *
   * @param message
   *          the message
   *
   */
  @ActionDoc(text = "Writes a plain message to the CEC device.")
  public static void writePlain(String message) {
    if (cec != null) {
      cec.writePlain(message);
    }
  }

  public static void setCecService(CecService cecService) {
    cec = cecService;
  }

}
