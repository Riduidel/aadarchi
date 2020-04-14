package org.ndx.agile.architecture.base;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;
import javax.inject.Inject;

import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.jboss.weld.config.ConfigurationKey;

import com.structurizr.Workspace;

@ApplicationScoped
public abstract class AbstractArchitecture {

	public static void main(Class<? extends AbstractArchitecture> toRun, String[] args) throws Throwable {
		// TODO Disable weld INFO logging, cause it outputs too much things of no interest
		// Disable the Weld thread pool (unless it is defined on command-line)
		System.setProperty(ConfigurationKey.EXECUTOR_THREAD_POOL_TYPE.get(), "NONE");
        SeContainerInitializer containerInit = SeContainerInitializer.newInstance();
        SeContainer container = containerInit.initialize();
        AbstractArchitecture architecture = container.select(toRun).get();
        architecture.run();
        container.close();
	}

	@Inject Logger logger;
	@Inject 
	@ConfigProperty(name = "agile.architecture.diagrams", defaultValue = "target/structurizr/architecture")
	File destination;
	@Inject ArchitectureEnhancer enhancer;

	/**
	 * Run method that will allow the description to be invoked and augmentations to be performed
	 * prior to have elements written. You should not have to overwrite this method.
	 * @throws IOException
	 */
	public void run() throws IOException {
		Workspace workspace = describeArchitecture();
		logger.info("Architecture has been described. Now enhancing it (including writing the diagrams)!");
		enhancer.enhance(workspace);
	}

	/**
	 * This is the method user has to implement.
	 * The whole goal is to let the architect write it and have pluggable extensions allowing easy enhancement of this architecture description.
	 * @return a structurizr workspace we will decorate and extend.
	 */
	protected abstract Workspace describeArchitecture();
}
