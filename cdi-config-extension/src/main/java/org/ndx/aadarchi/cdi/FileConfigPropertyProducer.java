package org.ndx.aadarchi.cdi;

import java.util.logging.Logger;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.ndx.aadarchi.cdi.deltaspike.BaseConfigPropertyProducer;
import org.ndx.aadarchi.cdi.deltaspike.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.InjectionPoint;
import jakarta.inject.Inject;

@ApplicationScoped
public class FileConfigPropertyProducer extends BaseConfigPropertyProducer {
	private static final Logger logger = Logger.getLogger(FileConfigPropertyProducer.class.getName());
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
		logger.info("Getting FileObject property value of "+injectionPoint);
		String configuredValue = getStringPropertyValue(injectionPoint);

		if(configuredValue==null) {
			throw new NullPointerException(String.format("Unable to inject a file which @ConfigProperty value is null at injection point %s", injectionPoint));
		} else {
			return fsManager.resolveFile(configuredValue.trim());
		}
	}
}
