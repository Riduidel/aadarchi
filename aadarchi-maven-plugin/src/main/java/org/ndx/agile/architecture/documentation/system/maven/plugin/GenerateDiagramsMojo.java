package org.ndx.agile.architecture.documentation.system.maven.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

@Mojo(name = "greet", defaultPhase = LifecyclePhase.PROCESS_RESOURCES)

public class GenerateDiagramsMojo extends AbstractMojo {

    @Parameter(required = true)
    public String name;

    @Parameter(required = false)
    public Boolean force;

    @Component
    private MavenProject mavenProject;

    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Hello " + name);
        getLog().info("Force : " + force);
        getLog().info("Version : " + mavenProject.getVersion() + "\nGroupId : " + mavenProject.getGroupId() + "\nArtifactId : " + mavenProject.getArtifactId());
    }
}