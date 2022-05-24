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
						element(name("gemPath"), "${project.build.directory}/gems"),
						element(name("attrributes"),
							element(name("allow-uri-read")),
							element(name("kroki-server-url"), "TODO create variable")
								),
						element(name("outputDirectory"), "${project.build.directory}/foo")
			    ),
			    executionEnvironment(
			        mavenProject,
			        mavenSession,
			        pluginManager
			    )
			);
	}
}
