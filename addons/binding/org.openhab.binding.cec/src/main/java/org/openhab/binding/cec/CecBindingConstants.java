/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.cec;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link cecBinding} class defines common constants, which are used across the whole binding.
 * 
 * @author Andreas Gebauer - Initial contribution
 */
public class CecBindingConstants {

  public static final String BINDING_ID = "cec";

  // List of all Thing Type UIDs
  public static final ThingTypeUID THING_TYPE_TV = new ThingTypeUID(BINDING_ID, "tv");
  public static final ThingTypeUID THING_TYPE_PLAYBACK = new ThingTypeUID(BINDING_ID, "playback");
  public static final ThingTypeUID THING_TYPE_TUNER = new ThingTypeUID(BINDING_ID, "tuner");
  public static final ThingTypeUID THING_TYPE_RECORDING = new ThingTypeUID(BINDING_ID, "recording");
  public static final ThingTypeUID THING_TYPE_AUDIO = new ThingTypeUID(BINDING_ID, "audio");

  // List of all Channel ids
  public static final String POWER = "power";
  public static final String POWERSTATUS = "powerStatus";
  public static final String ACTIVE_SOURCE = "activeSource";

  public static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = new HashSet<>(
      Arrays.asList(THING_TYPE_TV, THING_TYPE_AUDIO, THING_TYPE_PLAYBACK, THING_TYPE_RECORDING,
          THING_TYPE_TUNER));

  public static final Set<String> SUPPORTED_CHANNELS = new HashSet<>(
      Arrays.asList(POWER, POWERSTATUS, ACTIVE_SOURCE));

}
