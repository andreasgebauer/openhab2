/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.cec.internal.device;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.openhab.binding.cec.internal.protocol.data.PhysicalAddress;

/**
 * The CEC bridge.
 * 
 * @author Andreas Gebauer - Initial contribution
 */
public class CecBridge {

  private PropertyChangeSupport pcs;

  private PhysicalAddress activeSource;

  public CecBridge() {
    this.pcs = new PropertyChangeSupport(this);
  }

  public void setActiveSource(PhysicalAddress activeSource) {
    this.pcs.firePropertyChange("activeSource", this.activeSource, activeSource);
    this.activeSource = activeSource;
  }

  public void registerListener(PropertyChangeListener listener) {
    this.pcs.addPropertyChangeListener(listener);
  }

}
