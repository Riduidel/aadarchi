package org.ndx.aadarchi.base;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.se.SeContainer;
import jakarta.enterprise.inject.se.SeContainerInitializer;
import jakarta.inject.Inject;

import org.jboss.weld.config.ConfigurationKey;
import org.ndx.aadarchi.base.providers.FromDsl;

import com.structurizr.Workspace;
import com.structurizr.annotation.Component;
import com.structurizr.annotation.UsesComponent;

/**
 * Main class of agile architecture documentation system.
 * This will start a CDI container and, in this CDI container, instanciate this object.
 * THis instanciation will load {@link #enhancer} to generate all asciidoc required content.
 * @author nicolas-delsaux
 *
 */
@Component(technology = "Java, CDI")
@ApplicationScoped
public class ArchitectureDocumentationBuilder {

	public static void main(String[] args) throws Throwable {
		Thread.currentThread().setContextClassLoader(ArchitectureDocumentationBuilder.class.getClassLoader());
		// TODO Disable weld INFO logging, cause it outputs too much things of no interest
		// Disable the Weld thread pool (unless it is defined on command-line)
		System.setProperty(ConfigurationKey.EXECUTOR_THREAD_POOL_TYPE.get(), "NONE");
        SeContainerInitializer containerInit = SeContainerInitializer.newInstance();
        SeContainer container = containerInit.initialize();
        ArchitectureDocumentationBuilder architecture = container.select(ArchitectureDocumentationBuilder.class).get();
        architecture.run();
        container.close();
	}

	@Inject Logger logger;
	@Inject @UsesComponent(description = "Adds information to initial architecture description") ArchitectureEnhancer enhancer;
	@Inject @UsesComponent(description = "Generates initial architecture description") @Any Instance<ArchitectureModelProvider> availableProviders;
	@Inject @UsesComponent(description = "Read architecture description from workspace.dsl file") FromDsl fromDsl;
	@Inject @UsesComponent(description="Uses all enhancers") Instance<Enhancer> enhancers;

	/**
	 * Run method that will allow the description to be invoked and augmentations to be performed
	 * prior to have elements written. You should not have to overwrite this method.
	 * @throws IOException
	 */
	public void run() throws IOException {
		Workspace workspace = getArchitecture();
		logger.info("Architecture has been described. Now enhancing it (including writing the diagrams)!");
		enhancer.enhance(workspace, enhancers);
	}

	private Workspace getArchitecture() {
		for(ArchitectureModelProvider provider : availableProviders) {
			// I don't yet know how to ensure fromDsl is used as last injected value
			if(!fromDsl.equals(provider)) {
				try {
					return provider.describeArchitecture();
				} catch(Throwable e) {
					logger.log(Level.WARNING, 
							String.format("model provider %s failed to load any workspace", provider.getClass().getName()), 
							e);
				}
			}
		}
		try {
			return fromDsl.describeArchitecture();
		} catch(Throwable t) {
			logger.log(Level.WARNING, 
					String.format("model provider %s failed to load any workspace", fromDsl.getClass().getName()), 
					t);
		}
		throw new UnsupportedOperationException("There is no instance of ArchitectureModelProvider defined in project, and no workspace.dsl file to parse");
	}
}
