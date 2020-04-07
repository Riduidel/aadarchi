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
	@ConfigProperty(name = "maven.source.dir", defaultValue = "src/main/java") 
	File mavenSourceDir;
	@Inject 
	@ConfigProperty(name = "agile.architecture.diagrams", defaultValue = "target/structurizr/architecture")
	File destination;
	@Inject @ConfigProperty(name = "force", defaultValue = "false") boolean force;
	@Inject ArchitectureEnhancer enhancer;

	/**
	 * Run method that will allow the description to be invoked and augmentations to be performed
	 * prior to have elements written. You should not have to overwrite this method.
	 * @throws IOException
	 */
	public void run() throws IOException {
		// Unless force is set we will only generate architecture info if the current class source file
		// is more recent than generated class
		File source = new File(mavenSourceDir, getClass().getName().replace('.', '/')+".java");
		if(destination.exists()) {
			// Compare with the first generated diagram
			Optional<Long> oldestDiagramDate = Arrays.asList(destination.listFiles()).stream()
				.map(file -> file.lastModified())
				.sorted()
				.findFirst();
			if(oldestDiagramDate.isPresent()) {
				// Let's consider things can be slow some times, no ?
				// In other words, if I run a slow maven build
				if(oldestDiagramDate.get()>source.lastModified()) {
					if(!force) {
						logger.warning(String.format("Oldest diagram is more recent than architecture source file. Using cached architecture diagrams"));
						return;
					}
				}
			}
		}
		logger.info(String.format("We should write output to %s", destination.getAbsolutePath()));
		Workspace workspace = describeArchitecture();
		
		enhancer.enhance(workspace);
	}

	/**
	 * This is the method user has to implement.
	 * The whole goal is to let the architect write it and have pluggable extensions allowing easy enhancement of this architecture description.
	 * @return a structurizr workspace we will decorate and extend.
	 */
	protected abstract Workspace describeArchitecture();
}
