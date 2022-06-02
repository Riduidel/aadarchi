package org.ndx.agile.architecture.documentation.system.maven.plugin;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import static org.twdata.maven.mojoexecutor.MojoExecutor.*;

@Mojo(name = "generate-html-docs", defaultPhase = LifecyclePhase.PREPARE_PACKAGE)
public class GenerateHtmlDocs extends AbstractMojo {
	@Component
	private MavenProject mavenProject;

	@Component
	private MavenSession mavenSession;

	@Component
	private BuildPluginManager pluginManager;

	@Parameter(name = "asciidoctor-maven-plugin-version", defaultValue = "2.1.0")
	private String asciidoctorMavenPluginVersion;

	@Parameter(name = "asciidoctorj-pdf-version", defaultValue = "1.5.4")
	private String asciidoctorjPdfVersion;

	@Parameter(name = "jruby-version", defaultValue = "9.2.9.0")
	private String jrubyVersion;

	@Parameter(name = "asciidoctorj-version", defaultValue = "2.4.3")
	private String asciidoctorjVersion;

	@Parameter(name = "kroki-server-url", defaultValue = "${kroki.server.url}")
	private String krokiServerUrl;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		executeMojo(
			    plugin(
			        groupId("org.asciidoctor"),
			        artifactId("asciidoctor-maven-plugin"),
			        version(asciidoctorMavenPluginVersion),
			        dependencies(
							dependency("org.asciidoctor", "asciidoctorj-pdf", asciidoctorjPdfVersion),
							dependency("org.jruby", "jruby-complete", jrubyVersion),
							dependency("org.asciidoctor", "asciidoctorj", asciidoctorjVersion)
					)
			    ),
			    goal("process-asciidoc"),
			    configuration(
			    		// TODO conditionalize that invocation : add all gems dependencies here
			    		element(name("requires"),
			    				element(name("require"), "asciidoctor-kroki")),
						element(name("gemPath"), "${project.build.directory}/gems"),
						element(name("attributes"),
								element(name("allow-uri-read")), // allow to include distant content in the created document
								element(name("kroki-server-url"), krokiServerUrl),

								element(name("structurizrdir"), "${agile.architecture.output.diagrams}"),
								element(name("imagesdir"), "./images"),
								element(name("toc"), "left"), // put the table of content on the left side of the window
								element(name("icons"), "font"), // allow to use icons from "fonticones"
								element(name("sectanchors"), "true"), // sections behave like anchors/links to move around the document
								element(name("idseparator"), "-"), // put a separator between identifiers pieces
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
						element(name("outputDirectory"), "${project.build.directory}/foo") // define the path where the html files will get created
			    ),
			    executionEnvironment(
			        mavenProject,
			        mavenSession,
			        pluginManager
			    )
			);
	}
}
