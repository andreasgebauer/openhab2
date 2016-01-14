package org.openhab.binding.forecast;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ForecastBindingActivator implements BundleActivator {

    private static final Logger LOG = LoggerFactory.getLogger(ForecastBindingActivator.class);

    @Override
    public void start(BundleContext context) throws Exception {
        LOG.debug("Starting ForecastBinding");
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        LOG.debug("Stopping ForecastBinding");
    }
}
