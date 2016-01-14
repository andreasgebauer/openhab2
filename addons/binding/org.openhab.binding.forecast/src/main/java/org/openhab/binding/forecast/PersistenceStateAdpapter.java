package org.openhab.binding.forecast;

import org.eclipse.smarthome.core.library.items.NumberItem;
import org.eclipse.smarthome.core.persistence.HistoricItem;
import org.eclipse.smarthome.model.persistence.extensions.PersistenceExtensions;
import org.joda.time.DateTime;

public class PersistenceStateAdpapter implements HistoricStateAdapter {

	public HistoricItem stateOn(NumberItem item, DateTime tsThen) {
		return PersistenceExtensions.historicState(item, tsThen);
	}

	public static HistoricStateAdapter instance() {
		return new PersistenceStateAdpapter();
	}
}
