package org.openhab.binding.forecast;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;

import net.sourceforge.openforecast.ForecastingModel;
import net.sourceforge.openforecast.Observation;

public class Main {

    public static void main(String[] args)
            throws IllegalArgumentException, SecurityException, ReflectiveOperationException {

        net.sourceforge.openforecast.DataSet dataSet = new net.sourceforge.openforecast.DataSet();
        dataSet.setTimeVariable("timestamp");

        ZonedDateTime now = ZonedDateTime.now();

        for (int i = 10; i >= 0; i--) {
            double inc = Math.pow(i, 2.0);
            // System.out.print(inc + ", ");
            double value = 50.0 + i;// + inc;
            Observation observation = new Observation(value);
            observation.setIndependentValue("timestamp", now.minusMinutes(i * 2).getLong(ChronoField.INSTANT_SECONDS));
            // observation.setIndependentValue("number", -i);
            dataSet.add(observation);

            System.out.print(value + ", ");
        }

        ForecastingModel createModel = ForecastAction.createModel(
                "net.sourceforge.openforecast.models.PolynomialRegressionModel", new Object[] { "timestamp", 5 });

        createModel.init(dataSet);

        System.out.println();

        for (int i = 1; i <= 11; i++) {
            Observation observation = new Observation(0.0);
            observation.setIndependentValue("timestamp", now.plusMinutes(2 * i).getLong(ChronoField.INSTANT_SECONDS));
            // observation.setIndependentValue("number", i);

            double forecast = createModel.forecast(observation);
            System.out.print(forecast + ", ");
        }
    }
}
