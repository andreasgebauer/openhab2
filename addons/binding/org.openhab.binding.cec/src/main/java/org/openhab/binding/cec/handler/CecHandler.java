/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.cec.handler;

import static org.openhab.binding.cec.CecBindingConstants.CHANNEL_1;

import java.util.Collection;

import org.eclipse.smarthome.config.discovery.DiscoveryListener;
import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.DiscoveryService;
import org.eclipse.smarthome.config.discovery.DiscoveryServiceRegistry;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link CecHandler} is responsible for handling commands, which are sent to one of the
 * channels.
 * 
 * @author Andreas Gebauer - Initial contribution
 */
public class CecHandler extends BaseThingHandler implements DiscoveryListener {

  private Logger logger = LoggerFactory.getLogger(CecHandler.class);

  private DiscoveryServiceRegistry discoveryServiceRegistry;

  /**
   * Default constructor.
   * 
   * @param thing the thing
   * @param discoveryServiceRegistry registry of discovery service
   */
  public CecHandler(Thing thing, DiscoveryServiceRegistry discoveryServiceRegistry) {
    super(thing);

    if (discoveryServiceRegistry != null) {
      this.discoveryServiceRegistry = discoveryServiceRegistry;
      this.discoveryServiceRegistry.addDiscoveryListener(this);
    }
  }

  /**
   * Handles a command.
   */
  @Override
  public void handleCommand(ChannelUID channelUid, Command command) {
    if (channelUid.getId().equals(CHANNEL_1)) {
      // TODO: handle command

      // Note: if communication with thing fails for some reason,
      // indicate that by setting the status with detail information
      // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
      // "Could not control device at IP address x.x.x.x");
    }
  }

  @Override
  public void thingDiscovered(DiscoveryService source, DiscoveryResult result) {

  }

  @Override
  public void thingRemoved(DiscoveryService source, ThingUID thingUID) {

  }

  @Override
  public Collection<ThingUID> removeOlderResults(DiscoveryService source, long timestamp,
      Collection<ThingTypeUID> thingTypeUIDs, ThingUID bridgeUID) {
    return null;
  }
}
