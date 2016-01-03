/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.cec;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OSGI CEC binding activator.
 * 
 * @author Andreas Gebauer - Initial contribution
 */
public class CecBindingActivator implements BundleActivator {

  private static final Logger LOG = LoggerFactory.getLogger(CecBindingActivator.class);

  @Override
  public void start(BundleContext context) throws Exception {
    LOG.debug("Starting Cec binding");
  }

  @Override
  public void stop(BundleContext context) throws Exception {
    LOG.debug("Stopping Cec binding");
  }

}
