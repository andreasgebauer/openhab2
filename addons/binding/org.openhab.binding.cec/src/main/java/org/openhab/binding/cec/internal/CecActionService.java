/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.cec.internal;

import org.eclipse.smarthome.model.script.engine.action.ActionService;
import org.openhab.binding.cec.CecAction;

/**
 * CEC action service.
 * 
 * @author Andreas Gebauer - Initial contribution
 */
public class CecActionService implements ActionService {

  private CecService cec;

  @Override
  public String getActionClassName() {
    return CecAction.class.getCanonicalName();
  }

  @Override
  public Class<?> getActionClass() {
    return CecAction.class;
  }

  public void setCecService(CecService cec) {
    this.cec = cec;

  }
}
