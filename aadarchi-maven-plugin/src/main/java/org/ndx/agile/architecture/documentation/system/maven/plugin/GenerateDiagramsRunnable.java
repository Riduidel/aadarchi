package org.ndx.agile.architecture.documentation.system.maven.plugin;

import java.io.IOException;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.ndx.agile.architecture.base.ArchitectureDocumentationBuilder;

@ApplicationScoped
public class GenerateDiagramsRunnable implements Runnable {

	@Inject Logger logger;

    @Inject ArchitectureDocumentationBuilder builder;

	@Override
	public void run() {
		try {
			builder.run();
		} catch (IOException e) {
			throw new RuntimeException("Unable to generate architecture documentation elements", e);
		}
	}

}
