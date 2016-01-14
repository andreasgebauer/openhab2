package org.openhab.binding.forecast;

import org.eclipse.smarthome.core.library.items.NumberItem;
import org.eclipse.smarthome.core.persistence.HistoricItem;
import org.joda.time.DateTime;

public interface HistoricStateAdapter {

	HistoricItem stateOn(NumberItem item, DateTime tsThen);

}
