package org.openhab.binding.forecast.internal;

import org.openhab.binding.forecast.ForecastAction;
import org.openhab.core.model.script.engine.action.ActionService;
import org.osgi.service.component.annotations.Component;

@Component
public class ForecastActionService implements ActionService {

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
}
