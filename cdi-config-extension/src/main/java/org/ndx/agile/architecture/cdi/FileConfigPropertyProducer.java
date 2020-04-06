package org.ndx.agile.architecture.cdi;

import java.io.File;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.apache.deltaspike.core.spi.config.BaseConfigPropertyProducer;

@ApplicationScoped
public class FileConfigPropertyProducer extends BaseConfigPropertyProducer {

	@Produces
	@Dependent
    @ConfigProperty(name = "ignored") // we actually don't need the name
	public File produceLocationId(InjectionPoint injectionPoint) {
		String configuredValue = getStringPropertyValue(injectionPoint);

		return new File(configuredValue.trim());
	}
}
