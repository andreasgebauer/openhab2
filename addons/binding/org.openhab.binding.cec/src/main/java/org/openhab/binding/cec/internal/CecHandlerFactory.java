/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.cec.internal;

import static org.openhab.binding.cec.CecBindingConstants.SUPPORTED_THING_TYPES_UIDS;

import java.util.Dictionary;

import org.eclipse.smarthome.config.discovery.DiscoveryServiceRegistry;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.openhab.binding.cec.internal.config.CecBindingConfiguration;
import org.openhab.binding.cec.internal.handler.CecHandler;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link CecHandlerFactory} is responsible for creating things and thing handlers.
 * 
 * @author Andreas Gebauer - Initial contribution
 */
public class CecHandlerFactory extends BaseThingHandlerFactory {

  private static final Logger LOG = LoggerFactory.getLogger(CecHandlerFactory.class);
  private DiscoveryServiceRegistry discoveryServiceRegistry;
  private CecService cecService;

  @Override
  protected void activate(ComponentContext componentContext) {
    super.activate(componentContext);
    Dictionary<String, Object> properties = componentContext.getProperties();

    CecBindingConfiguration config = new CecBindingConfiguration();

    String device = (String) properties.get(CecBindingConfiguration.DEVICE);
    if (device != null) {
      config.device = device;
    }

    String executable = (String) properties.get(CecBindingConfiguration.EXECUTABLE);
    if (executable != null) {
      config.executable = executable;
    }

    Integer refresh = Integer
        .valueOf((String) properties.get(CecBindingConfiguration.REFRESH_INTERVAL));
    if (refresh != null) {
      config.refreshInterval = refresh;
    }

    cecService.setup(config);
  }

  @Override
  public boolean supportsThingType(ThingTypeUID thingTypeUID) {
    return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
  }

  @Override
  protected ThingHandler createHandler(Thing thing) {
    LOG.debug("Creating thing handler for {}", thing);
    return new CecHandler(thing, cecService);
  }

  public void setCecService(CecService cecService) {
    this.cecService = cecService;
  }

  protected void setDiscoveryServiceRegistry(DiscoveryServiceRegistry discoveryServiceRegistry) {
    this.discoveryServiceRegistry = discoveryServiceRegistry;
  }

  protected void unsetDiscoveryServiceRegistry(DiscoveryServiceRegistry discoveryServiceRegistry) {
    this.discoveryServiceRegistry = null;
  }

}
