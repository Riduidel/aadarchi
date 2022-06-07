package org.ndx.agile.architecture.documentation.system.maven.plugin;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.ndx.agile.architecture.documentation.system.maven.cdi.helper.wrappers.AbstractCDIStarterMojo;
import org.ndx.agile.architecture.documentation.system.maven.cdi.helper.wrappers.MojoProduces;

@Mojo(name = "generate-model", 
	defaultPhase = LifecyclePhase.PREPARE_PACKAGE,
	requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class GenerateDiagramsMojo extends AbstractCDIStarterMojo {
	
	@MojoProduces
	@Parameter(name="agile-architecture-output-diagrams", defaultValue = "${project.build.directory}/structurizr/diagrams", property="agile.architecture.output.diagrams")
	public String agileArchitectureOutputDiagrams;

	@Override
	protected Class<? extends Runnable> getCDIEnabledRunnableClass() {
		return GenerateDiagramsRunnable.class;
	}

}