package org.ndx.agile.architecture.documentation.system.maven.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.ndx.agile.architecture.base.ArchitectureDocumentationBuilder;

import javax.inject.Inject;
import java.util.logging.Logger;

@Mojo(name = "generate-model", defaultPhase = LifecyclePhase.PROCESS_RESOURCES)
public class GenerateDiagramsMojo extends AbstractMojo {
    private static final Logger jul = Logger.getLogger(GenerateDiagramsMojo.class.getName());

    @Parameter(required = true)
    public String name;

    @Parameter(required = false)
    public boolean force;

    @Component
    private MavenProject mavenProject;

//    @Inject ArchitectureDocumentationBuilder architecture;

    public void execute() throws MojoExecutionException, MojoFailureException {
        // TODO bind current logging mechanism to Maven logging system
        // TODO Inject maven properties in CDI context (this should be done by creating a DeltaSpike injection point)
//        architecture.run();
        jul.info("This should appear as a maven lopg, and NOT as a jul log");
    }
}