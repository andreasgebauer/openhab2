package org.openhab.binding.forecast;

import java.math.BigDecimal;
import java.util.Iterator;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import net.sourceforge.openforecast.DataPoint;
import net.sourceforge.openforecast.DataSet;
import net.sourceforge.openforecast.Observation;

public class FixedDurationDataset extends DataSet {

	private Duration duration;

	public FixedDurationDataset(Duration duration) {
		this.duration = duration;
	}

	@Override
	public boolean add(DataPoint obj) {
		this.removeObsoleteData();
		return super.add(obj);
	}

	/**
	 * Adds a value with time now.
	 * 
	 * @param value
	 */
	public void add(BigDecimal value) {
		Observation obj = new Observation(value.doubleValue());
		obj.setIndependentValue("timestamp", DateTime.now().getMillis());
		this.add(obj);
	}

	private void removeObsoleteData() {
		DateTime start = getStart();

		Iterator<DataPoint> iterator = super.iterator();

		while (iterator.hasNext()) {
			DataPoint next = iterator.next();

			int timeMillis = (int) next.getIndependentValue("timestamp");
			if (start.getMillis() > timeMillis) {
				iterator.remove();
			}
		}
	}

	private DateTime getStart() {
		return DateTime.now().minus(duration);
	}
}
