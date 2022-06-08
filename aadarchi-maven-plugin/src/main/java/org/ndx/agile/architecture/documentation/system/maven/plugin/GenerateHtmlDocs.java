package org.ndx.agile.architecture.documentation.system.maven.plugin;

import static org.twdata.maven.mojoexecutor.MojoExecutor.element;
import static org.twdata.maven.mojoexecutor.MojoExecutor.name;

import java.io.File;
import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.ndx.agile.architecture.documentation.system.maven.plugin.asciidoctor.AbstractAsciidoctorCallingMojo;
import org.twdata.maven.mojoexecutor.MojoExecutor;
import org.twdata.maven.mojoexecutor.MojoExecutor.Element;

/**
 * Generates html version of the documents stored in source folder.
 * 
 * This mojo is a specific invocation of asciidoctor-maven-plugin with a "correct" set
 * of parameters for our use case
 * 
 * @author nicolas-delsaux
 *
 */
@Mojo(name = "generate-html-docs", defaultPhase = LifecyclePhase.PREPARE_PACKAGE)
public class GenerateHtmlDocs extends AbstractAsciidoctorCallingMojo {
	/**
	 * Input folder where asciidoc files are stored.
	 */
	@Parameter(name="html-docs-source-dir", defaultValue="${project.basedir}/src/docs/asciidoc", property = "asciidoc.source.docs.directory")
	private File htmlDocsSourceDir;
	/**
	 * Output folder where html docs are produced
	 */
	@Parameter(name="html-docs-output-dir", defaultValue="${project.build.directory}/asciidoc/docs/html", property = "asciidoc.target.html.docs.directory")
	private File htmlDocsOutputDir;
	
	@Override
	protected List<Dependency> dependencies() {
		return MojoExecutor.dependencies(
				dependencyJRuby(),
				dependencyAsciidoctor()
				);
	}

	@Override
	protected String getSourceDirectory() {
		return htmlDocsSourceDir.getAbsolutePath();
	}

	@Override
	protected String getOutputDirectory() {
		return htmlDocsOutputDir.getAbsolutePath();
	}

	@Override
	protected Element configurationBackend() {
		return element(name("backend"), "html5");
	}

}
