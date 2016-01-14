package org.openhab.binding.forecast;

import java.math.BigDecimal;

import org.eclipse.smarthome.core.library.items.NumberItem;
import org.eclipse.smarthome.core.library.types.DecimalType;

import net.sourceforge.openforecast.DataSet;
import net.sourceforge.openforecast.Observation;
import net.sourceforge.openforecast.models.MultipleLinearRegressionModel;

public class Main {

	public static void main(String[] args) {

		NumberItem item = new NumberItem("item");
		item.setState(new DecimalType(0.0d));
		String model = "net.sourceforge.openforecast.models.MultipleLinearRegressionModel";
		
		
		
		DataSet dataSet = new DataSet();
		
		int numberOfValues = 40;
		
		
		for (int i = 0; i < numberOfValues; i++) {
			dataSet.add(new Observation(20.0));
		}
		
		
		ForecastAction.doForecast(dataSet, new BigDecimal(60 * 40), 120, new MultipleLinearRegressionModel());
		
	}

}
