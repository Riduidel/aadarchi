package org.ndx.agile.architecture.documentation.system.maven.plugin;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.ndx.agile.architecture.documentation.system.maven.plugin.asciidoctor.AbstractAsciidoctorCallingMojo;
import org.twdata.maven.mojoexecutor.MojoExecutor;
import org.twdata.maven.mojoexecutor.MojoExecutor.Element;

import static org.twdata.maven.mojoexecutor.MojoExecutor.*;

import java.io.File;
import java.util.List;

@Mojo(name = "generate-pdf-docs", defaultPhase = LifecyclePhase.PREPARE_PACKAGE)
public class GeneratePdfDocs extends AbstractAsciidoctorCallingMojo {
	/**
	 * used version of the asciidoctorj-pdf rendering backend
	 * @see https://mvnrepository.com/artifact/org.asciidoctor/asciidoctorj-pdf
	 */
	@Parameter(name = "asciidoctorj-pdf-version", defaultValue = "1.5.4", property = "version.asciidoctorj.pdf")
	private String asciidoctorjPdfVersion;
	@Parameter(name="pdf-docs-source-dir", defaultValue="${project.basedir}/src/docs/asciidoc", property = "asciidoc.source.docs.directory")
	private File pdfDocsSourceDir;
	@Parameter(name="pdf-docs-target-dir", defaultValue="${project.build.directory}/asciidoc/docs/pdf", property = "asciidoc.target.pdf.docs.directory")
	private File pdfDocsTargetDir;

	protected List<Dependency> dependencies() {
		return MojoExecutor.dependencies(
				dependencyJRuby(),
				dependencyAsciidoctor(),
				dependency("org.asciidoctor", "asciidoctorj-pdf", asciidoctorjPdfVersion)
		);
	}

	@Override
	protected String getSourceDirectory() {
		return pdfDocsSourceDir.getAbsolutePath();
	}

	@Override
	protected String getOutputDirectory() {
		return pdfDocsTargetDir.getAbsolutePath();
	}

	@Override
	protected Element configurationBackend() {
		return element(name("backend"), "pdf");
	}
}
