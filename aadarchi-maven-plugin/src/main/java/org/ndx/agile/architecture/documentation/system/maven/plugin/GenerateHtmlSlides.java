package org.ndx.agile.architecture.documentation.system.maven.plugin;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.ndx.agile.architecture.documentation.system.maven.plugin.asciidoctor.AbstractAsciidoctorCallingMojo;
import org.twdata.maven.mojoexecutor.MojoExecutor;
import org.twdata.maven.mojoexecutor.MojoExecutor.Element;

import static org.twdata.maven.mojoexecutor.MojoExecutor.*;

import java.io.File;
import java.util.List;

@Mojo(name = "generate-html-slides", defaultPhase = LifecyclePhase.PREPARE_PACKAGE)
public class GenerateHtmlSlides extends AbstractAsciidoctorCallingMojo {
	@Parameter(name="html-slides-source-dir", defaultValue="${project.basedir}/src/slides/asciidoc", property = "asciidoc.source.slides.directory")
	private File htmlSlidesSourceDir;
	@Parameter(name="html-slides-output-dir", defaultValue="${project.build.directory}/asciidoc/slides/html", property = "asciidoc.target.html.slides.directory")
	private File htmlSlidesOutputDir;
	@Parameter(name="revealjs-version", defaultValue="4.3.1", property="version.revealjs")
	private String revealjsVersion;

	/**
	 * Used version of asciidoctorj-revealjs embedder
	 * @see https://mvnrepository.com/artifact/org.asciidoctor/asciidoctorj-revealjs
	 */
	@Parameter(name = "asciidoctorj-revealjs-version", defaultValue = "5.0.0.rc1", property="version.asciidoctorj.revealjs")
	private String asciidoctorjRevealjsVersion;
	
	@Override
	protected List<Dependency> dependencies() {
		return MojoExecutor.dependencies(
				dependencyJRuby(),
				dependencyAsciidoctor(),
				dependency("org.asciidoctor", "asciidoctorj-revealjs", asciidoctorjRevealjsVersion)
				);
	}


	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().debug("Downloading revealjs");
		executeMojo(
				plugin(
					groupId("com.googlecode.maven-download-plugin"),
					artifactId("download-maven-plugin"),
					version("1.6.8")),
				goal("wget"), 
				MojoExecutor.configuration(
						element(name("uri"), String.format("https://github.com/hakimel/reveal.js/archive/%s.zip", revealjsVersion)),
						element(name("unpack"), "true"),
						element(name("outputFileName"), String.format("reveal.js-${version.revealjs}.zip", revealjsVersion)),
						element(name("outputDirectory"), "${asciidoc.target.slides.directory}")), 
				executionEnvironment());
		getLog().debug("Generating slides");
		super.execute();
	}

	protected Element requiredGems() {
		return element(name("requires"),
				element(name("require"), "asciidoctor-kroki"),
				element(name("require"), "asciidoctor-revealjs")
				);
	}


	@Override
	protected String getSourceDirectory() {
		return htmlSlidesSourceDir.getAbsolutePath();
	}


	@Override
	protected String getOutputDirectory() {
		return htmlSlidesOutputDir.getAbsolutePath();
	}


	@Override
	protected Element configurationBackend() {
		return element(name("backend"), "revealjs");
	}
}
