package org.openhab.binding.forecast;

import java.time.ZonedDateTime;

import org.openhab.core.library.items.NumberItem;
import org.openhab.core.persistence.HistoricItem;

public interface HistoricStateAdapter {

    HistoricItem stateOn(NumberItem item, ZonedDateTime tsThen);
}
