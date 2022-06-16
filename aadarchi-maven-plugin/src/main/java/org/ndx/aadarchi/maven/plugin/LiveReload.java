package org.ndx.aadarchi.maven.plugin;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys.ConfigProperties.WorkspaceDsl;
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

	/**
	 * Port used for livereload of generated documentation
	 */
	@Parameter(name="livereload-port", defaultValue="35729")
	private int livereloadPort;
	
	/**
	 * List of goals to execute after live-reload.
	 * As it is a list, the default value is not provided in a way compatible with maven annotation.
	 * 
	 * The default value for that goal is
	 * 
	 * <ul>
	 * <li>prepare-package</li>
	 * <li>"io.github.Riduidel.agile-architecture-documentation-system:aadarchi-maven-plugin@generate-html-docs"</li>
	 * <li>"io.github.Riduidel.agile-architecture-documentation-system:aadarchi-maven-plugin@generate-html-slides"</li>
	 * </ul>
	 */
	@Parameter(name="goals-to-execute")
	private List<String> goalsToExecute = Arrays.asList("prepare-package",
			"io.github.Riduidel.agile-architecture-documentation-system:aadarchi-maven-plugin@generate-html-docs",
			"io.github.Riduidel.agile-architecture-documentation-system:aadarchi-maven-plugin@generate-html-slides"
			);

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		try (ServerSocket test = new ServerSocket(livereloadPort)){
			getLog().debug(String.format("port %d can be used", livereloadPort));
		} catch (IOException e) {
			throw new MojoFailureException(String.format("port %d not available", livereloadPort));
		}

		Path docroot = FileSystems.getDefault().getPath(mavenProject.getBuild().getDirectory());
		Executors.newSingleThreadExecutor().execute(() -> {
			try {
				new LRServer(livereloadPort, docroot).run(); // == start() + join()
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
								goals()
						)
				),
				executionEnvironment(
						mavenProject,
						mavenSession,
						pluginManager
				)
		);
	}
	
	private Element[] goals() {
		// First, get our artifact name
		return goalsToExecute.stream()
				.map(text -> element(name("goal"), text))
				.toArray(Element[]::new);
	}

	private Element[] watches() {
		List<File> files = new ArrayList<File>(Arrays.asList(
			javaSourcesDir,
			htmlDocsSourceDir,
			htmlSlidesSourceDir,
			architectureDsl.getParentFile()
			));
		
		for(Resource r : mavenProject.getResources()) {
			files.add(new File(r.getDirectory()));
		}

		return files.stream()
				.filter(File::exists)
				.map(File::getAbsolutePath)
				.map(text -> element(name("watch"), element(name("directory"), text))).toArray(Element[]::new);
	}
}
