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
import org.openhab.binding.cec.internal.protocol.data.PowerStatus;

/**
 * @author Andreas Gebauer - Initial contribution
 */
public class CecDevice {

  private String osdName = "<UNSET>";
  private Vendor vendor;
  private DeviceType deviceType;
  private int cecVersion;
  private PowerStatus powerStatus;
  private PhysicalAddress physicalAddress;
  private String menuLanguage;
  private boolean power = false;

  private PropertyChangeSupport pcs;

  public CecDevice(DeviceType type) {
    this.deviceType = type;
    this.pcs = new PropertyChangeSupport(this);
  }

  public void setOsdName(final String osdName) {
    pcs.firePropertyChange("osdName", this.osdName, osdName);
    this.osdName = osdName;
  }

  public void setVendor(final Vendor vendor) {
    pcs.firePropertyChange("vendor", this.vendor, vendor);
    this.vendor = vendor;
  }

  public DeviceType getDeviceType() {
    return deviceType;
  }

  public void setDeviceType(DeviceType deviceType) {
    pcs.firePropertyChange("deviceType", this.deviceType, deviceType);
    this.deviceType = deviceType;
  }

  public void setCecVersion(int cecVersion) {
    pcs.firePropertyChange("cecVersion", this.cecVersion, cecVersion);
    this.cecVersion = cecVersion;
  }

  public PowerStatus getPowerStatus() {
    return powerStatus;
  }

  public void setPowerStatus(PowerStatus powerStatus) {
    pcs.firePropertyChange("powerStatus", this.powerStatus, powerStatus);
    this.powerStatus = powerStatus;

    if (this.powerStatus == PowerStatus.ON) {
      this.setPower(true);
    } else if (this.powerStatus == PowerStatus.STANDBY) {
      this.setPower(false);
    }
  }

  private void setPower(boolean power) {
    pcs.firePropertyChange("power", this.power, power);
    this.power = power;
  }

  public void setPhysicalAddress(final PhysicalAddress physicalAddress) {
    pcs.firePropertyChange("physicalAddress", this.physicalAddress, physicalAddress);
    this.physicalAddress = physicalAddress;
  }

  public String getOsdName() {
    return this.osdName;
  }

  public String getMenuLanguage() {
    return menuLanguage;
  }

  public void setMenuLanguage(String menuLanguage) {
    pcs.firePropertyChange("menuLanguage", this.menuLanguage, menuLanguage);
    this.menuLanguage = menuLanguage;
    // this.setPowerStatus(PowerStatus.ON);
  }

  public void registerListener(PropertyChangeListener listener) {
    this.pcs.addPropertyChangeListener(listener);
  }

  @Override
  public String toString() {
    if (deviceType == DeviceType.BROADCAST) {
      return deviceType.toString();
    }
    return deviceType + ": " + osdName + " (" + vendor + ")";
  }
}
