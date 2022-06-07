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

@Mojo(name = "livereload", defaultPhase = LifecyclePhase.PREPARE_PACKAGE)
public class LiveReload extends AbstractMojo {

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
								dependency("org.asciidoctor", "asciidoctorj", asciidoctorjVersion)
						)
				),
				goal("process-asciidoc"),
				configuration(
			    		// TODO conditionalize that invocation : add all gems dependencies here
			    		element(name("requires"),
								element(name("require"), "asciidoctor-kroki")),
						element(name("gemPath"), "${project.build.directory}/gems")
				),
				executionEnvironment(
						mavenProject,
						mavenSession,
						pluginManager
				)
		);
	}
}
