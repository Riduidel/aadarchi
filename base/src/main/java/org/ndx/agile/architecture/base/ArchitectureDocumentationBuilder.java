package org.ndx.agile.architecture.base;

import java.io.IOException;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.apache.commons.configuration2.BaseConfiguration;
import org.apache.commons.configuration2.CompositeConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.EnvironmentConfiguration;
import org.apache.commons.configuration2.ImmutableConfiguration;
import org.apache.commons.configuration2.SystemConfiguration;

import com.structurizr.Workspace;
import com.structurizr.annotation.Component;
import com.structurizr.annotation.UsesComponent;

/**
 * Main class of agile architecture documentation system.
 * This will start a CDI container and, in this CDI container, instanciate this object.
 * THis instanciation will load {@link #provider} and {@link #enhancer} to generate all asciidoc required content.
 * @author nicolas-delsaux
 *
 */
@Component(technology = "Java/CDI")
public class ArchitectureDocumentationBuilder {

	public static void main(String[] args) throws Throwable {
        CompositeConfiguration configuration = new CompositeConfiguration();
        configuration.addConfiguration(new EnvironmentConfiguration());
        configuration.addConfiguration(new SystemConfiguration());
        configuration.addConfiguration(commandLineArgsAsConfiguration(args));
        ArchitectureDocumentationBuilder architecture = new ArchitectureDocumentationBuilder(configuration);
        architecture.run();
	}

	private static Configuration commandLineArgsAsConfiguration(String[] args) {
		var map = commandLineToMap(args);
		BaseConfiguration returned = new BaseConfiguration();
		map.entrySet().forEach(entry -> returned.setProperty(entry.getKey(), entry.getValue()));
		return returned;
	}

	private static Map<String, Object> commandLineToMap(String[] args) {
		Map<String, Object> returned = new TreeMap<String, Object>();
		for (int i = 0; i < args.length; i+=2) {
			if(args.length>i+1) {
				returned.put(args[i], args[i+1]);
			}
		}
		return returned;
	}

	private static final Logger logger = Logger.getLogger(ArchitectureDocumentationBuilder.class.getName());
	@UsesComponent(description = "Adds information to initial architecture description") ArchitectureEnhancer enhancer;
	@UsesComponent(description = "Generates initial architecture description") ArchitectureModelProvider provider;


	public ArchitectureDocumentationBuilder(ImmutableConfiguration configuration) {
		enhancer = new ArchitectureEnhancer(configuration);
		provider = ServiceLoader.load(ArchitectureModelProvider.class).findFirst().orElseThrow(() -> new AgileArchitectureException("Unable to find an instance of ArchitectureModelProvider using ServiceLoader") {
		});
	}

	/**
	 * Run method that will allow the description to be invoked and augmentations to be performed
	 * prior to have elements written. You should not have to overwrite this method.
	 * @throws IOException
	 */
	public void run() throws IOException {
		Workspace workspace = provider.describeArchitecture();
		logger.info("Architecture has been described. Now enhancing it (including writing the diagrams)!");
		enhancer.enhance(workspace);
	}
}
