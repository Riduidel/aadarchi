package org.ndx.agile.architecture.documentation.system.maven.plugin;

import java.io.IOException;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.ndx.agile.architecture.base.ArchitectureDocumentationBuilder;

import com.itemis.maven.plugins.cdi.CDIMojoProcessingStep;
import com.itemis.maven.plugins.cdi.ExecutionContext;
import com.itemis.maven.plugins.cdi.annotations.ProcessingStep;

@ProcessingStep(id = "generate diagrams")
public class GenerateDiagramsStep implements CDIMojoProcessingStep {

	@Inject Logger logger;

    @Inject ArchitectureDocumentationBuilder builder;

	@Override
	public void execute(ExecutionContext context) throws MojoExecutionException, MojoFailureException {
		try {
			builder.run();
		} catch (IOException e) {
			throw new MojoFailureException("Unable to generate architecture documentation elements", e);
		}
	}

}
