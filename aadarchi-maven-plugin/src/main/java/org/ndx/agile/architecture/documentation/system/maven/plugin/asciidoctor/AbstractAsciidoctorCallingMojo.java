package org.ndx.agile.architecture.documentation.system.maven.plugin.asciidoctor;

import static org.twdata.maven.mojoexecutor.MojoExecutor.artifactId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.dependency;
import static org.twdata.maven.mojoexecutor.MojoExecutor.groupId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.plugin;
import static org.twdata.maven.mojoexecutor.MojoExecutor.version;

import java.util.List;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.twdata.maven.mojoexecutor.MojoExecutor;
import org.twdata.maven.mojoexecutor.MojoExecutor.ExecutionEnvironment;

/**
 * Base class for all mojos invoking asciidoctor-maven-plugin.
 * It provides a set of common method useful to easily configure and execute the asciidoctor-maven-plugin
 * @author nicolas-delsaux
 *
 */
public abstract class AbstractAsciidoctorCallingMojo extends AbstractMojo {

	@Component
	private MavenProject mavenProject;
	@Component
	private MavenSession mavenSession;
	@Component
	private BuildPluginManager pluginManager;
	/**
	 * Version of the asciidoctor-maven-plugin
	 * @see https://mvnrepository.com/artifact/org.asciidoctor/asciidoctor-maven-plugin
	 */
	@Parameter(name = "asciidoctor-maven-plugin-version", defaultValue = "2.1.0", property="version.asciidoctor.maven.plugin")
	private String asciidoctorMavenPluginVersion;
	/**
	 * Used version of JRuby interpreter
	 * @see https://mvnrepository.com/artifact/org.jruby/jruby
	 */
	@Parameter(name = "jruby-version", defaultValue = "9.2.9.0", property="version.jruby")
	private String jrubyVersion;
	/**
	 * Used version of asciidoctor Java implementation
	 * @see https://mvnrepository.com/artifact/org.asciidoctor/asciidoctorj 
	 */
	@Parameter(name = "asciidoctorj-version", defaultValue = "2.4.3", property = "version.asciidoctorj")
	private String asciidoctorjVersion;

	protected ExecutionEnvironment executionEnvironment() {
		return MojoExecutor.executionEnvironment(
		    mavenProject,
		    mavenSession,
		    pluginManager
		);
	}

	protected Plugin asciidoctorMavenPlugin() {
		return plugin(
				groupId("org.asciidoctor"),
				artifactId("asciidoctor-maven-plugin"),
				version(asciidoctorMavenPluginVersion),
				dependencies()
		);
	}
	
	/**
	 * As each plugin requires its own set of dependencies, this method is left abstract
	 * @return
	 */
	protected abstract List<Dependency> dependencies();

	protected final Dependency dependencyAsciidoctor() {
		return dependency("org.asciidoctor", "asciidoctorj", asciidoctorjVersion);
	}

	protected final Dependency dependencyJRuby() {
		return dependency("org.jruby", "jruby-complete", jrubyVersion);
	}

}
