package org.ndx.agile.architecture.documentation.system.maven.plugin;

import static org.twdata.maven.mojoexecutor.MojoExecutor.artifactId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.configuration;
import static org.twdata.maven.mojoexecutor.MojoExecutor.element;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executeMojo;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executionEnvironment;
import static org.twdata.maven.mojoexecutor.MojoExecutor.goal;
import static org.twdata.maven.mojoexecutor.MojoExecutor.groupId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.name;
import static org.twdata.maven.mojoexecutor.MojoExecutor.plugin;
import static org.twdata.maven.mojoexecutor.MojoExecutor.version;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;.*

@Mojo(name = "generate-html-docs", defaultPhase = LifecyclePhase.PREPARE_PACKAGE)
public class GenerateHtmlDocs extends AbstractMojo {
	@Component
	private MavenProject mavenProject;

	@Component
	private MavenSession mavenSession;

	@Component
	private BuildPluginManager pluginManager;

	@Parameter(name = "asciidoctor-maven-plugin-version", defaultValue = "2.1.0")
	private String asciidoctorMavenPluinVersion;
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		executeMojo(
			    plugin(
			        groupId("org.asciidoctor"),
			        artifactId("asciidoctor-maven-plugin"),
			        version(asciidoctorMavenPluinVersion),
			        dependencies(...)
			    ),
			    goal("copy-dependencies"),
			    configuration(
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
