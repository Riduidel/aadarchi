package org.ndx.agile.architecture.documentation.system.maven.plugin;

import com.itemis.maven.plugins.cdi.AbstractCDIMojo;
import com.itemis.maven.plugins.cdi.ExecutionContext;
import com.itemis.maven.plugins.cdi.annotations.MojoProduces;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.util.Arrays;
import java.util.logging.Logger;


@Mojo(name = "generate-model", defaultPhase = LifecyclePhase.PROCESS_RESOURCES)
public class GenerateDiagramsMojo extends AbstractCDIMojo {

	@Parameter(defaultValue = "${project}", readonly = true, required = true)
	@MojoProduces
	private MavenProject project;


	public void execute(ExecutionContext context) throws MojoExecutionException, MojoFailureException {
		Logger root = Logger.getLogger("");
		Arrays.stream(root.getHandlers()).forEach(root::removeHandler);
		root.addHandler(new MavenLoggingRedirectorHandler(getLog()));
	}
}