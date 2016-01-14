package org.openhab.binding.forecast;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.ClassUtils;
import org.eclipse.smarthome.core.library.items.NumberItem;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.persistence.HistoricItem;
import org.eclipse.smarthome.model.script.engine.action.ActionDoc;
import org.eclipse.smarthome.model.script.engine.action.ParamDoc;
import org.joda.time.DateTime;
import org.openhab.binding.forecast.internal.ForecastActionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.openforecast.EvaluationCriteria;
import net.sourceforge.openforecast.Forecaster;
import net.sourceforge.openforecast.ForecastingModel;
import net.sourceforge.openforecast.Observation;

public class ForecastAction {

	private static final Logger LOG = LoggerFactory.getLogger(ForecastAction.class);

	private static BigDecimal THOUSAND = new BigDecimal(1000);

	@ActionDoc(text = "Create a forecasting model")
	public static ForecastingModel createModel(String className, Object[] params) throws RuntimeException {
		ClassLoader cl = ForecastAction.class.getClassLoader();
		// ClassLoader cl = Thread.currentThread().getContextClassLoader();

		try {
			Constructor<?>[] constructors = cl.loadClass(className).getConstructors();
			conLoop: for (Constructor<?> con : constructors) {
				Class<?>[] conParams = con.getParameterTypes();
				if (params == null && conParams.length == 0) {
					LOG.debug("No constructor params given. using constructor {}", con);
					return (ForecastingModel) con.newInstance();
				} else if (conParams.length == params.length) {
					LOG.debug("Parameter lengths fit");
					for (int i = 0; i < conParams.length; i++) {
						Class<?> conParam = conParams[i];
						Object givenParam = params[i];
						LOG.debug("Checking wether constructor param {} fits constructor param {}", givenParam,
								conParam);
						if (!ClassUtils.isAssignable(givenParam.getClass(), conParam, true)) {
							LOG.debug("Given constructor param {} does not fit constructor param {}", givenParam,
									conParam);
							continue conLoop;
						}
					}
					LOG.debug("Will use constructor {}", con);
					Object newInstance = con.newInstance(params);
					return (ForecastingModel) newInstance;
				}
			}

		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}

		throw new IllegalArgumentException(
				"No constructor found for class " + className + " and params " + Arrays.asList(params));
	}

	@ActionDoc(text = "make forecast")
	public static List<Number> forecast(NumberItem item, int secondsTotal, int numberOfValues,
			int numberOfValuesForecast) {
		BigDecimal secondsBetween = getMillisBetweenSamples(secondsTotal, numberOfValues);
		net.sourceforge.openforecast.DataSet historicalDataSet = historicalDataSet(item, secondsBetween, numberOfValues,
				PersistenceStateAdpapter.instance());

		return createNumberList(forecastWithCriteria(historicalDataSet, secondsBetween, numberOfValuesForecast, null));
	}

	@ActionDoc(text = "make forecast with criteria")
	public static List<Number> forecastWithCriteria(@ParamDoc(name = "", text = "") NumberItem item,
			@ParamDoc(name = "", text = "") int secondsTotal, @ParamDoc(name = "", text = "") int numberOfValues,
			@ParamDoc(name = "", text = "") int numberOfValuesForecast,
			@ParamDoc(name = "", text = "") String criteriaSring) {

		BigDecimal millis = getMillisBetweenSamples(secondsTotal, numberOfValues);

		net.sourceforge.openforecast.DataSet dataSet = historicalDataSet(item, millis, numberOfValues,
				PersistenceStateAdpapter.instance());

		EvaluationCriteria criteria = null;
		if (criteriaSring != null) {
			try {
				criteria = (EvaluationCriteria) EvaluationCriteria.class.getField(criteriaSring)
						.get(EvaluationCriteria.class);

			} catch (ReflectiveOperationException | SecurityException e) {
				throw new RuntimeException(e);
			}
		}

		return createNumberList(forecastWithCriteria(dataSet, millis, numberOfValuesForecast, criteria));
	}

	@ActionDoc(text = "make forecast with model")
	public static DataSet forecastWithModel(@ParamDoc(name = "", text = "") NumberItem item,
			@ParamDoc(name = "", text = "") int secondsTotal, @ParamDoc(name = "", text = "") int numberOfValues,
			@ParamDoc(name = "", text = "") int numberOfValuesForecast,
			@ParamDoc(name = "", text = "") String modelString, Object... params) {
		ForecastingModel model = createModel(modelString, params);
		return forecast(item, secondsTotal, numberOfValues, numberOfValuesForecast, model);
	}

	@ActionDoc(text = "make forecast with model")
	public static DataSet forecast(@ParamDoc(name = "", text = "") NumberItem item,
			@ParamDoc(name = "", text = "") int secondsTotal, @ParamDoc(name = "", text = "") int numberOfValues,
			@ParamDoc(name = "", text = "") int numberOfValuesForecast,
			@ParamDoc(name = "", text = "") ForecastingModel model) {
		BigDecimal millis = getMillisBetweenSamples(secondsTotal, numberOfValues);

		net.sourceforge.openforecast.DataSet dataSet = historicalDataSet(item, millis, numberOfValues,
				PersistenceStateAdpapter.instance());

		return doForecast(dataSet, millis, numberOfValuesForecast, model);
	}

	@ActionDoc(text = "make forecast with model and return number list")
	public static List<Number> forecastWithModelAsNumberList(@ParamDoc(name = "", text = "") NumberItem item,
			@ParamDoc(name = "", text = "") int secondsTotal, @ParamDoc(name = "", text = "") int numberOfValues,
			@ParamDoc(name = "", text = "") int numberOfValuesForecast,
			@ParamDoc(name = "", text = "") ForecastingModel model) {
		return createNumberList(forecast(item, secondsTotal, numberOfValues, numberOfValuesForecast, model));
	}

	@ActionDoc(text = "make forecast with model and return number list")
	public static List<Number> forecastWithModelAsNumberList(@ParamDoc(name = "", text = "") NumberItem item,
			@ParamDoc(name = "", text = "") int secondsTotal, @ParamDoc(name = "", text = "") int numberOfValues,
			@ParamDoc(name = "", text = "") int numberOfValuesForecast,
			@ParamDoc(name = "", text = "") String modelString, //
			@ParamDoc(name = "", text = "") Object[] params) {
		ForecastingModel model = createModel(modelString, params);
		return forecastWithModelAsNumberList(item, secondsTotal, numberOfValues, numberOfValuesForecast, model);
	}

	public static net.sourceforge.openforecast.DataSet historicalDataSet(NumberItem item, BigDecimal millisBetween,
			int numberOfValues, HistoricStateAdapter adapter) {

		net.sourceforge.openforecast.DataSet dataSet = new net.sourceforge.openforecast.DataSet();
		dataSet.setTimeVariable("timestamp");

		DateTime now = DateTime.now();

		dataSet.add(getObservation((DecimalType) item.getState(), new Date(), 0));
		for (int i = 1; i <= numberOfValues; i++) {
			DateTime tsThen = now.minusMillis(millisBetween.multiply(new BigDecimal(i)).intValue());

			HistoricItem historicState = adapter.stateOn(item, tsThen);
			if (historicState != null) {
				DecimalType state = (DecimalType) historicState.getState();
				Date timestamp = historicState.getTimestamp();

				dataSet.add(getObservation(state, timestamp, -i));
			}
		}

		return dataSet;
	}

	public static DataSet doForecast(net.sourceforge.openforecast.DataSet dataSet, BigDecimal millisBetween,
			int numberOfValuesForecast, ForecastingModel forecastingModel) {

		forecastingModel.init(dataSet);

		LOG.debug("Using forecast model {}", forecastingModel);

		return new DataSet(forecastingModel.forecast(createForecastDataSet(millisBetween, numberOfValuesForecast)));
	}

	public static List<Number> createNumberList(DataSet dataSet) {
		List<Number> data = new ArrayList<>();
		for (org.openhab.binding.forecast.DataPoint dataPoint : dataSet) {
			double dependentValue = dataPoint.getValue();
			data.add(dependentValue);
		}
		return data;
	}

	// private

	private static DataSet forecastWithCriteria(net.sourceforge.openforecast.DataSet dataSet, BigDecimal millisBetween,
			int numberOfValuesForecast, EvaluationCriteria criteria) {

		ForecastingModel forecastModel;
		if (criteria != null)
			forecastModel = Forecaster.getBestForecast(dataSet, criteria);
		else
			forecastModel = Forecaster.getBestForecast(dataSet);

		return doForecast(dataSet, millisBetween, numberOfValuesForecast, forecastModel);
	}

	private static net.sourceforge.openforecast.DataSet createForecastDataSet(BigDecimal millisBetween,
			int numberOfValuesForecast) {
		DateTime now = DateTime.now();

		net.sourceforge.openforecast.DataSet fcDataset = new net.sourceforge.openforecast.DataSet();
		for (int i = 0; i < numberOfValuesForecast; i++) {
			Observation observation = new Observation(0.0);
			DateTime tsThen = now.plusMillis(millisBetween.multiply(new BigDecimal(i + 1)).intValue());
			observation.setIndependentValue("timestamp", tsThen.getMillis());
			observation.setIndependentValue("number", i + 1);
			fcDataset.add(observation);
		}
		return fcDataset;
	}

	private static BigDecimal getMillisBetweenSamples(int secondsTotal, int numberOfValues) {
		return new BigDecimal(secondsTotal).divide(new BigDecimal(numberOfValues)).multiply(THOUSAND);
	}

	private static Observation getObservation(DecimalType decimal, Date date, int index) {
		Observation observation = new Observation(decimal.doubleValue());
		observation.setIndependentValue("timestamp", date.getTime());
		observation.setIndependentValue("number", index);
		return observation;
	}

	public static void setService(ForecastActionService forecastActionService) {
		// TODO Auto-generated method stub

	}

}
