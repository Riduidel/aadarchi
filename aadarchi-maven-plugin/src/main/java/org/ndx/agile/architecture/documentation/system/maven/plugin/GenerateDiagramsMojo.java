package org.ndx.agile.architecture.documentation.system.maven.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.Arrays;
import java.util.logging.Logger;

@org.apache.maven.plugins.annotations.Mojo(name = "generate-model", defaultPhase = LifecyclePhase.PROCESS_RESOURCES)
public class GenerateDiagramsMojo extends AbstractMojo {
    private static final Logger logger = Logger.getLogger(GenerateDiagramsMojo.class.getName());

    @Parameter(required = true)
    public String name;

    public void execute() throws MojoExecutionException, MojoFailureException {
        Logger root = Logger.getLogger("");
        Arrays.stream(root.getHandlers()).forEach(root::removeHandler);
        root.addHandler(new MavenLoggingRedirectorHandler(getLog()));
        logger.info("info msg");
        logger.warning("warning msg");
        logger.config("config msg");
        logger.fine("fine msg");
        logger.finer("finer msg");
        logger.finest("finest msg");
        logger.severe("severe  msg");
    }

}