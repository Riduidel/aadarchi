package org.ndx.agile.architecture.documentation.system.maven.plugin;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.project.MavenProject;

import com.itemis.maven.plugins.cdi.AbstractCDIMojo;
import com.itemis.maven.plugins.cdi.annotations.MojoProduces;

@Mojo(name = "generate-model", defaultPhase = LifecyclePhase.PROCESS_RESOURCES)
public class GenerateDiagramsMojo extends AbstractCDIMojo {

	@Component
	@MojoProduces
	private MavenProject project;

	@Component
	@MojoProduces
	private MavenSession session;
	
	@Component
	@MojoProduces private MojoExecution mojoExecution;
}