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

import static org.twdata.maven.mojoexecutor.MojoExecutor.*;

import java.util.List;

@Mojo(name = "generate-html-slides", defaultPhase = LifecyclePhase.PREPARE_PACKAGE)
public class GenerateHtmlSlides extends AbstractAsciidoctorCallingMojo {

	/**
	 * Used version of asciidoctorj-revealjs embedder
	 * @see https://mvnrepository.com/artifact/org.asciidoctor/asciidoctorj-revealjs
	 */
//	@Parameter(name = "asciidoctorj-revealjs-version", defaultValue = "4.1.0", property="version.asciidoctorj.revealjs")
//	private String asciidoctorRevealVersion;
	
	@Parameter(name = "kroki-server-url", defaultValue = "${kroki.server.url}")
	private String krokiServerUrl;
	
	@Override
	protected List<Dependency> dependencies() {
		return MojoExecutor.dependencies(
				dependencyJRuby(),
				dependencyAsciidoctor(),
				dependency("org.asciidoctor", "asciidoctorj-revealjs", "5.0.0.rc1")
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
				configuration(
						element(name("uri"), "https://github.com/hakimel/reveal.js/archive/${version.revealjs}.zip"),
						element(name("unpack"), "true"),
						element(name("outputFileName"), "reveal.js-${version.revealjs}.zip"),
						element(name("outputDirectory"), "${asciidoc.target.slides.directory}")), 
				executionEnvironment());
		getLog().debug("Generating slides");
		executeMojo(
				asciidoctorMavenPlugin(),
			    goal("process-asciidoc"),
			    configuration(
			    		// TODO conditionalize that invocation : add all gems dependencies here
			    		element(name("requires"),
			    				element(name("require"), "asciidoctor-revealjs"),
			    				element(name("require"), "asciidoctor-kroki")),
						element(name("gemPath"), "${project.build.directory}/gems"),
						element(name("attributes"),
								element(name("allow-uri-read")), // allow to include distant content in the created document
								element(name("kroki-server-url"), krokiServerUrl),

								element(name("plantumldir"), "images/"),
								element(name("structurizrdir"), "${agile.architecture.output.diagrams}"),
								element(name("imagesdir"), "."),
								element(name("revealjsdir"), "reveal.js-${version.revealjs}"),
								element(name("sourcedir"), "${basedir}/src/main/java"),
								element(name("revealjs_theme"), "solarized"),
								element(name("stylesheet")),
								element(name("hideBugReport"), "${asciidoc.documents.hide.bug.report}"), // add link to allow users to report some bugs

								element(name("sectnums"), "true"), // display section number in the summary
								element(name("revnumber"), "${project.version}"), // add project version in the footer
								element(name("revdate"), "${maven.build.timestamp}"), // add the date in the footer

								element(name("project-group-id"), "${project.groupId}"), // catch the groupId defined in the pom.xml file
								element(name("project-artifact-id"), "${project.artifactId}"), // catch the artifactId defined in the pom.xml file
								element(name("project-name"), "${project.name}"), // catch the project name defined in the pom.xml file
								element(name("project-version"), "${project.version}"), // catch the project version defined in the pom.xml file
								element(name("project-build-timestamp"), "${maven.build.timestamp}"), // catch the timestamp defined when maven build
								element(name("project-pom-path"), "../../../pom.xml"), // catch pom.xml file path
								element(name("project-issues-on-github"), "${issues.url}"), // catch the issue url defined in the pom.xml file
								element(name("organization"), "${project.organization.name}"), // catch the organization name defined in the pom.xml file
								element(name("enhancements-dir"), "${agile.architecture.output.enhancements}") // catch the path to the enhancements directory defined in the pom.xml file
						),
						element(name("backend"), "revealjs"),
						element(name("sourceDirectory"), "${asciidoc.source.slides.directory}"), // define the path where the html files will get created
						element(name("outputDirectory"), "${asciidoc.target.slides.directory}") // define the path where the html files will get created
			    ),
			    executionEnvironment()
			);
	}
}
