package org.ndx.agile.architecture.documentation.system.maven.plugin;

import java.util.Arrays;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.itemis.maven.plugins.cdi.CDIMojoProcessingStep;
import com.itemis.maven.plugins.cdi.ExecutionContext;
import com.itemis.maven.plugins.cdi.annotations.ProcessingStep;

@ProcessingStep(id = "generate diagrams")
public class GenerateDiagramsStep implements CDIMojoProcessingStep {

//	private static final Logger logger = Logger.getLogger(GenerateDiagramsMojo.GenerateDiagramsMojo.class.getName());
	@Inject Logger logger;

    @Inject @ConfigProperty(name = "project.version") private String projectVersion;

	@Override
	public void execute(ExecutionContext context) throws MojoExecutionException, MojoFailureException {
//		Logger root = Logger.getLogger("");
//		Arrays.stream(root.getHandlers()).forEach(root::removeHandler);
//		root.addHandler(new MavenLoggingRedirectorHandler(getLog()));
		logger.info("project version is " + projectVersion);
	}

}
