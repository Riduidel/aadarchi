package org.ndx.aadarchi.cdi;

import java.io.File;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.apache.deltaspike.core.spi.config.BaseConfigPropertyProducer;

@ApplicationScoped
public class FileConfigPropertyProducer extends BaseConfigPropertyProducer {

	/**
	 * When an injection point is declared with the @ConfigProperty annotation and has the File type,
	 * this method creates a file using the configured value as name
	 * @param injectionPoint
	 * @return
	 */
	@Produces
	@Dependent
    @ConfigProperty(name = "ignored") // we actually don't need the name
	public File produceLocationId(InjectionPoint injectionPoint) {
		String configuredValue = getStringPropertyValue(injectionPoint);

		if(configuredValue==null) {
			throw new NullPointerException(String.format("Unable to inject a file which @ConfigProperty value is null at injection point %s", injectionPoint));
		} else {
			return new File(configuredValue.trim());
		}
	}
}
