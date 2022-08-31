package org.ndx.aadarchi.maven.plugin.asciidoctor;

import static org.twdata.maven.mojoexecutor.MojoExecutor.executeMojo;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.twdata.maven.mojoexecutor.MojoExecutor;
import org.twdata.maven.mojoexecutor.MojoExecutor.ExecutionEnvironment;

public abstract class AbstractMojoExecutorMojo extends AbstractMojo {

	@Component
	protected MavenProject mavenProject;
	@Component
	protected MavenSession mavenSession;
	@Component
	protected BuildPluginManager pluginManager;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		executeMojo(
				executedMavenPlugin(),
				executedMojo(),
				configuration(),
				executionEnvironment()
		);
	}

	protected abstract Xpp3Dom configuration();

	protected abstract String executedMojo();

	protected abstract Plugin executedMavenPlugin();

	protected ExecutionEnvironment executionEnvironment() {
		return MojoExecutor.executionEnvironment(
		    mavenProject,
		    mavenSession,
		    pluginManager
		);
	}

}
