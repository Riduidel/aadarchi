package org.ndx.agile.architecture.documentation.system.maven.plugin;

import static org.twdata.maven.mojoexecutor.MojoExecutor.dependency;
import static org.twdata.maven.mojoexecutor.MojoExecutor.element;
import static org.twdata.maven.mojoexecutor.MojoExecutor.name;

import java.io.File;
import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.ndx.agile.architecture.documentation.system.maven.plugin.asciidoctor.AbstractAsciidoctorCallingMojo;
import org.twdata.maven.mojoexecutor.MojoExecutor;
import org.twdata.maven.mojoexecutor.MojoExecutor.Element;

/**
 * Generates pdf version of the documents stored in source folder.
 * 
 * This mojo is a specific invocation of asciidoctor-maven-plugin with a "correct" set
 * of parameters for our use case
 * 
 * @author nicolas-delsaux
 */
@Mojo(name = "generate-pdf-docs", defaultPhase = LifecyclePhase.PREPARE_PACKAGE)
public class GeneratePdfDocs extends AbstractAsciidoctorCallingMojo {
	/**
	 * used version of the asciidoctorj-pdf rendering backend
	 * @see https://mvnrepository.com/artifact/org.asciidoctor/asciidoctorj-pdf
	 */
	@Parameter(name = "asciidoctorj-pdf-version", defaultValue = "1.5.4", property = "version.asciidoctorj.pdf")
	private String asciidoctorjPdfVersion;
	/**
	 * Input folder where asciidoc files are stored.
	 */
	@Parameter(name="pdf-docs-source-dir", defaultValue="${project.basedir}/src/docs/asciidoc", property = "asciidoc.source.docs.directory")
	private File pdfDocsSourceDir;
	/**
	 * Output folder where pdf docs are produced
	 */
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
