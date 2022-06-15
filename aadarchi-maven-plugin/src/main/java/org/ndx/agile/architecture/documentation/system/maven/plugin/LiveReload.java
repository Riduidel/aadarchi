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

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

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
import org.ndx.agile.architecture.base.enhancers.ModelElementKeys.ConfigProperties.WorkspaceDsl;
import org.twdata.maven.mojoexecutor.MojoExecutor.Element;

import net_alchim31_livereload.LRServer; //#from net.alchim31:livereload-jvm:0.2.0

@Mojo(name = "livereload", defaultPhase = LifecyclePhase.PACKAGE)
public class LiveReload extends AbstractMojo {

	@Component
	private MavenProject mavenProject;

	@Component
	private MavenSession mavenSession;

	@Component
	private BuildPluginManager pluginManager;
	/**
	 * Input folder where asciidoc files are stored.
	 */
	@Parameter(name="html-docs-source-dir", defaultValue="${project.basedir}/src/docs/asciidoc", property = "asciidoc.source.docs.directory")
	private File htmlDocsSourceDir;
	/**
	 * Input folder where asciidoc files for slides are stored
	 */
	@Parameter(name="html-slides-source-dir", defaultValue="${project.basedir}/src/slides/asciidoc", property = "asciidoc.source.slides.directory")
	private File htmlSlidesSourceDir;
	/**
	 * Input workspace.dsl file
	 */
	@Parameter(name="architecture-dsl", defaultValue=WorkspaceDsl.VALUE, property = WorkspaceDsl.NAME)
	private File architectureDsl;
	@Parameter(defaultValue="${project.build.sourceDirectory}", readonly = true, required = true)
	private File javaSourcesDir;

	@Parameter(name="livereload-port", defaultValue="35729")
	private int LivereloadPort;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		int port = LivereloadPort;

		try (ServerSocket test = new ServerSocket(port)){
			getLog().debug(String.format("port %d can be used", port));
		} catch (IOException e) {
			throw new MojoFailureException(String.format("port %d not available", port));
		}

		Path docroot = FileSystems.getDefault().getPath(mavenProject.getBuild().getDirectory());
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
		List<File> files = Arrays.asList(
			javaSourcesDir,
			htmlDocsSourceDir,
			htmlSlidesSourceDir,
			architectureDsl.getParentFile()
			);
		// TODO add resources
		mavenProject.getResources().stream()
			.map(resource -> null);

		return files.stream()
				.filter(File::exists)
				.map(File::getAbsolutePath)
				.map(text -> element(name("watch"), element(name("directory"), text))).toArray(Element[]::new);
	}
}
