package org.ndx.aadarchi.maven.plugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.ndx.aadarchi.base.ArchitectureDocumentationBuilder;
import org.ndx.aadarchi.base.ArchitectureModelProvider;
import org.ndx.aadarchi.maven.cdi.helper.wrappers.AbstractCDIStarterMojo;
import org.ndx.aadarchi.maven.cdi.helper.wrappers.MojoProduces;

import com.structurizr.annotation.Component;
import com.structurizr.annotation.UsesComponent;

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
@Component(technology = "Java, maven")
@Mojo(name = "generate-model", 
	defaultPhase = LifecyclePhase.PREPARE_PACKAGE,
	requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class GenerateDiagramsMojo extends AbstractCDIStarterMojo {
	
	/**
	 * Output directory for the generated diagrams
	 */
	@MojoProduces
	@Parameter(name="agile-architecture-output-diagrams", defaultValue = "${project.build.directory}/structurizr/diagrams", property="aadarchi.output.diagrams")
	public String agileArchitectureOutputDiagrams;
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		// Mind you, Deltaspike relies upon the presence of a context classloader to choose which
		// property loaders are available, which sometimes prevent the
		// github token to be injected
		Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
		super.execute();
	}

	@Override
	protected Class<? extends Runnable> getCDIEnabledRunnableClass() {
		return GenerateDiagramsRunnable.class;
	}

}