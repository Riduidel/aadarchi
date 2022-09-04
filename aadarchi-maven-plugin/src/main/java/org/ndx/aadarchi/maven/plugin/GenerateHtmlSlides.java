package org.ndx.aadarchi.maven.plugin;

import static org.twdata.maven.mojoexecutor.MojoExecutor.artifactId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.dependency;
import static org.twdata.maven.mojoexecutor.MojoExecutor.element;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executeMojo;
import static org.twdata.maven.mojoexecutor.MojoExecutor.goal;
import static org.twdata.maven.mojoexecutor.MojoExecutor.groupId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.name;
import static org.twdata.maven.mojoexecutor.MojoExecutor.plugin;
import static org.twdata.maven.mojoexecutor.MojoExecutor.version;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.ndx.aadarchi.maven.plugin.asciidoctor.AbstractAsciidoctorCallingMojo;
import org.twdata.maven.mojoexecutor.MojoExecutor;
import org.twdata.maven.mojoexecutor.MojoExecutor.Element;

@Mojo(name = "generate-html-slides", defaultPhase = LifecyclePhase.PREPARE_PACKAGE)
public class GenerateHtmlSlides extends AbstractAsciidoctorCallingMojo {
	/**
	 * Input folder where asciidoc files for slides are stored
	 */
	@Parameter(name="html-slides-source-dir", defaultValue="${project.basedir}/src/slides/asciidoc", property = "asciidoc.source.slides.directory")
	private File htmlSlidesSourceDir;
	/**
	 * Output folder where html slides are produced
	 */
	@Parameter(name="html-slides-output-dir", defaultValue="${project.build.directory}/asciidoc/slides/html", property = "asciidoc.target.html.slides.directory")
	private File htmlSlidesOutputDir;
	/**
	 * Used version of revealjs
	 * @see https://github.com/hakimel/reveal.js/releases
	 */
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
		String outputPath = htmlSlidesOutputDir.getAbsolutePath();
		getLog().info("Downloading revealjs into "+outputPath);
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
						element(name("outputDirectory"), outputPath)), 
				executionEnvironment());
		getLog().info("Generating slides");
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
	
	@Override
	protected Xpp3Dom configuration() {
		Xpp3Dom returned = super.configuration();
		returned.addChild(element(name("templateDirs"), 
				String.format("%s/reveal.js-%s/templates/slim", htmlSlidesOutputDir.getAbsolutePath(), revealjsVersion)
					).toDom());
		return returned;
	}
	
	@Override
	public List<Element> configurationAttributes() {
		List<Element> returned = new ArrayList<>(super.configurationAttributes());
		returned.add(element(name("revealjsdir"), String.format("reveal.js-%s", revealjsVersion)));
		return returned;
	}
}
