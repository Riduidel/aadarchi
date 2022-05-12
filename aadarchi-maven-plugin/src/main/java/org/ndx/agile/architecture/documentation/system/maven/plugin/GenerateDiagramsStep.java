package org.ndx.agile.architecture.documentation.system.maven.plugin;

import com.itemis.maven.plugins.cdi.CDIMojoProcessingStep;
import com.itemis.maven.plugins.cdi.ExecutionContext;
import com.itemis.maven.plugins.cdi.annotations.ProcessingStep;
import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import javax.inject.Inject;
import java.util.logging.Logger;

@ProcessingStep(id = "generate diagrams")
public class GenerateDiagramsStep implements CDIMojoProcessingStep {

	@Inject Logger logger;

    @Inject @ConfigProperty(name = "project.version") private String projectVersion;

	@Override
	public void execute(ExecutionContext context) throws MojoExecutionException, MojoFailureException {
		logger.info("project version is " + projectVersion);
	}

}
