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

	@Component
	private MavenProject mavenProject;

	@Component
	private MavenSession mavenSession;

	@Component
	private BuildPluginManager pluginManager;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		executeMojo(
				plugin(
						groupId("org.codehaus.mojo"),
						artifactId("exec-maven-plugin"),
						version(""),
						dependencies(
								dependency("net.alchim31", "livereload-jvm", "0.2.0")
						)
				),
				goal("exec"),
				configuration(
						element(name("executable"), "java"),
						element(name("arguments"),
								element(name("argument"), "-classpath"),
								element(name("classpath"),
										element(name("dependency"), "net.alchim31:livereload-jvm"),
										element(name("dependency"), "org.eclipse.jetty:jetty-util"),
										element(name("dependency"), "org.eclipse.jetty:jetty-io"),
										element(name("dependency"), "org.eclipse.jetty.orbit:javax.servlet:jar"),
										element(name("dependency"), "org.eclipse.jetty:jetty-continuation"),
										element(name("dependency"), "org.eclipse.jetty:jetty-server"),
										element(name("dependency"), "org.eclipse.jetty.orbit:javax.servlet"),
										element(name("dependency"), "org.eclipse.jetty:jetty-http"),
										element(name("dependency"), "com.googlecode.json-simple:json-simple"),
										element(name("dependency"), "org.eclipse.jetty:jetty-websocket")),
								element(name("argument"), "net_alchim31_livereload.Main"),
								element(name("argument"), "-"),
								element(name("argument"), "${asciidoc.target.base.directory}")),
						element(name("async"), "true"),
						element(name("asyncDestroyOnShutdown"), "true")
				),
				executionEnvironment(
						mavenProject,
						mavenSession,
						pluginManager
				)
		);
	}
}
