package org.ndx.agile.architecture.documentation.system.maven.plugin;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.ndx.agile.architecture.base.ArchitectureDocumentationBuilder;
import org.ndx.agile.architecture.base.ArchitectureModelProvider;
import org.ndx.agile.architecture.documentation.system.maven.cdi.helper.wrappers.AbstractCDIStarterMojo;
import org.ndx.agile.architecture.documentation.system.maven.cdi.helper.wrappers.MojoProduces;

/**
 * Generates the various model diagrams and elements by processing the architecture description
 * made either in a workspace.dsl file or through an {@link ArchitectureModelProvider} implementing class.
 * 
 * In fact, this mojo "simply" starts a CDI container in which the 
 * {@link ArchitectureDocumentationBuilder} class is invoked with all available
 * elements of classpath loaded.
 * 
 * Notice that there are many more options than the parameter given below.
 * 
 * @author nicolas-delsaux
 *
 */
@Mojo(name = "generate-model", 
	defaultPhase = LifecyclePhase.PREPARE_PACKAGE,
	requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class GenerateDiagramsMojo extends AbstractCDIStarterMojo {
	
	/**
	 * Output directory for the generated diagrams
	 */
	@MojoProduces
	@Parameter(name="agile-architecture-output-diagrams", defaultValue = "${project.build.directory}/structurizr/diagrams", property="agile.architecture.output.diagrams")
	public String agileArchitectureOutputDiagrams;

	@Override
	protected Class<? extends Runnable> getCDIEnabledRunnableClass() {
		return GenerateDiagramsRunnable.class;
	}

}