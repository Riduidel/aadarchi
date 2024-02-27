package org.ndx.aadarchi.maven.plugin;

import java.io.IOException;
import java.util.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.ndx.aadarchi.base.ArchitectureDocumentationBuilder;

import com.structurizr.annotation.Component;
import com.structurizr.annotation.UsesComponent;
@Component(technology = "Java, CDI")
@ApplicationScoped
public class GenerateDiagramsRunnable implements Runnable {

	@Inject Logger logger;

	@UsesComponent(description = "generates architecture documentation")
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
