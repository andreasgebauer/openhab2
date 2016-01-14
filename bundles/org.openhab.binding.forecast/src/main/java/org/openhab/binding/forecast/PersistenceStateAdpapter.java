package org.openhab.binding.forecast;

import java.time.ZonedDateTime;

import org.openhab.core.library.items.NumberItem;
import org.openhab.core.persistence.HistoricItem;
import org.openhab.core.persistence.extensions.PersistenceExtensions;

public class PersistenceStateAdpapter implements HistoricStateAdapter {

    public HistoricItem stateOn(NumberItem item, ZonedDateTime tsThen) {
        return PersistenceExtensions.historicState(item, tsThen);
    }

    public static HistoricStateAdapter instance() {
        return new PersistenceStateAdpapter();
    }
}
