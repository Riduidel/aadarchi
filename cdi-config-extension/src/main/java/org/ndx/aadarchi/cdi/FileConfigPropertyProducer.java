package org.ndx.aadarchi.cdi;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.apache.deltaspike.core.spi.config.BaseConfigPropertyProducer;

@ApplicationScoped
public class FileConfigPropertyProducer extends BaseConfigPropertyProducer {
	@Inject FileSystemManager fsManager;

	/**
	 * When an injection point is declared with the @ConfigProperty annotation and has the File type,
	 * this method creates a file using the configured value as name
	 * @param injectionPoint place where a file should be injected
	 * @return the file object we want in this injection point
	 * @throws FileSystemException 
	 */
	@Produces
	@Dependent
    @ConfigProperty(name = "ignored") // we actually don't need the name
	public FileObject produceLocationId(InjectionPoint injectionPoint) throws FileSystemException {
		String configuredValue = getStringPropertyValue(injectionPoint);

		if(configuredValue==null) {
			throw new NullPointerException(String.format("Unable to inject a file which @ConfigProperty value is null at injection point %s", injectionPoint));
		} else {
			return fsManager.resolveFile(configuredValue.trim());
		}
	}
}
