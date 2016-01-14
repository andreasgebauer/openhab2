package org.openhab.binding.forecast.internal;

import org.eclipse.smarthome.model.persistence.extensions.PersistenceExtensions;
import org.eclipse.smarthome.model.script.engine.action.ActionService;
import org.openhab.binding.forecast.ForecastAction;

public class ForecastActionService implements ActionService {

	private PersistenceExtensions persistenceExtension;

	public ForecastActionService() {
		ForecastAction.setService(this);
	}

	@Override
	public String getActionClassName() {
		return ForecastAction.class.getCanonicalName();
	}

	@Override
	public Class<?> getActionClass() {
		return ForecastAction.class;
	}

	public void setPersistenceExtension(PersistenceExtensions persistenceExtension) {
		this.persistenceExtension = persistenceExtension;

	}

	public PersistenceExtensions getPersistenceExtensions() {
		return this.persistenceExtension;
	}
}
