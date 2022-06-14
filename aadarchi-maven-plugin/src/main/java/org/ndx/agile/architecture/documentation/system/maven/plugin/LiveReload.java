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

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.concurrent.Executors;

import static org.twdata.maven.mojoexecutor.MojoExecutor.*;
import net_alchim31_livereload.LRServer; //#from net.alchim31:livereload-jvm:0.2.0

@Mojo(name = "livereload", defaultPhase = LifecyclePhase.PACKAGE)
public class LiveReload extends AbstractMojo {

	@Component
	private MavenProject mavenProject;

	@Component
	private MavenSession mavenSession;

	@Component
	private BuildPluginManager pluginManager;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		int port = 35729;

		Path docroot = FileSystems.getDefault().getPath(mavenProject.getBuild().getOutputDirectory());
		Executors.newSingleThreadExecutor().execute(() -> {
			try {
				new LRServer(port, docroot).run(); // == start() + join()
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});

		executeMojo(
				plugin(
						groupId("com.fizzed"),
						artifactId("fizzed-watcher-maven-plugin"),
						version("1.0.6")
				),
				goal("run"),
				configuration(
						element(name("touchFile"), "target/watcher.touchfile"),
						element(name("watches"),
								watches()
						),
						element(name("goals"),
								element(name("goal"), "prepare-package"),
								element(name("goal"), "org.asciidoctor:asciidoctor-maven-plugin:process-asciidoc@generate-slides"),
								element(name("goal"), "org.asciidoctor:asciidoctor-maven-plugin:process-asciidoc@generate-html-doc")
						)
				),
				executionEnvironment(
						mavenProject,
						mavenSession,
						pluginManager
				)
		);

	}

	private Element[] watches() {
		Element[] watch = Element.asList;
				element(name("watch"),
				element(name("directory"), "${project.basedir}/src/architecture/resources")
		),
		element(name("watch"),
				element(name("directory"), "${project.basedir}/src/main/java")
		),
		element(name("watch"), 
				element(name("directory"), "${project.basedir}/src/main/resources")
		),
		element(name("watch"),
				element(name("directory"), "${asciidoc.source.docs.directory}")
		),
		element(name("watch"),
				element(name("directory"), "${asciidoc.source.slides.directory}")
		)];
				return watch;
	}
}
