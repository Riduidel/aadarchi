package org.ndx.agile.architecture.documentation.system.maven.plugin;

import com.itemis.maven.plugins.cdi.CDIMojoProcessingStep;
import com.itemis.maven.plugins.cdi.ExecutionContext;
import com.itemis.maven.plugins.cdi.annotations.ProcessingStep;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.logging.Logger;

@ProcessingStep(id = "initialize logger")
public class InitializeLoggerStep implements CDIMojoProcessingStep {
    @Inject
    private Log log;

    @Inject Logger logger;

    @Override
    public void execute(ExecutionContext context) throws MojoExecutionException, MojoFailureException {
        Logger root = Logger.getLogger("");
        Arrays.stream(root.getHandlers()).forEach(root::removeHandler);
        root.addHandler(new MavenLoggingRedirectorHandler(log));
        logger.info("msg de test");
    }
}
