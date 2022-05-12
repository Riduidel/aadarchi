package org.ndx.agile.architecture.documentation.system.maven.plugin;

import com.itemis.maven.plugins.cdi.AbstractCDIMojo;
import com.itemis.maven.plugins.cdi.annotations.MojoProduces;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;


@Mojo(name = "generate-model", defaultPhase = LifecyclePhase.PROCESS_RESOURCES)
public class GenerateDiagramsMojo extends AbstractCDIMojo {

	@Parameter(defaultValue = "${project}", readonly = true, required = true)
	@MojoProduces
	private MavenProject project;
}