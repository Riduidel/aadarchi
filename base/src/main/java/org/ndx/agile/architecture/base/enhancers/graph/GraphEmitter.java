package org.ndx.agile.architecture.base.enhancers.graph;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.ndx.agile.architecture.base.ArchitectureEnhancer;
import org.ndx.agile.architecture.base.OutputBuilder;
import org.ndx.agile.architecture.base.ViewEnhancer;

import com.structurizr.Workspace;
import com.structurizr.annotation.Component;
import com.structurizr.io.plantuml.C4PlantUMLWriter;
import com.structurizr.io.plantuml.PlantUMLWriter;
import com.structurizr.view.View;
import com.structurizr.view.ViewSet;

/**
 * Generates all graph and output them in the {@link #destination} folder
 * @author nicolas-delsaux
 *
 */
@Component(technology = "Java/CDI")
@ApplicationScoped
public class GraphEmitter implements ViewEnhancer {
	@Inject Logger logger;
	@Inject 
	@ConfigProperty(name = "agile.architecture.diagrams", defaultValue = "target/structurizr/architecture")
	File destination;
	@Inject @ConfigProperty(name = "force", defaultValue = "false") boolean force;

	@Override
	public boolean isParallel() {
		return true;
	}

	@Override
	public int priority() {
		return Integer.MAX_VALUE;
	}

	@Override public boolean startVisit(ViewSet viewset) { return true; }

	@Override
	public boolean startVisit(View s) { return false; }

	@Override
	public void endVisit(View s, OutputBuilder builder) {}

	@Override
	public void endVisit(ViewSet viewset, OutputBuilder builder) {
		logger.info(String.format("All views should have been output to %s", destination.getAbsolutePath()));
	}

	@Override
	public boolean startVisit(Workspace workspace, OutputBuilder builder) {
		return true;
	}

	@Override
	public void endVisit(Workspace workspace, OutputBuilder builder) {
		PlantUMLWriter plantUMLWriter = new C4PlantUMLWriter();
		// Hints to have arrows more easily visible
		plantUMLWriter.addSkinParam("pathHoverColor", "GreenYellow");
		plantUMLWriter.addSkinParam("ArrowThickness", "3");
		plantUMLWriter.addSkinParam("svgLinkTarget", "_parent");
		
		destination.mkdirs();
		plantUMLWriter.toPlantUMLDiagrams(workspace).stream().parallel()
			.forEach(diagram -> {
				Path path = new File(destination, diagram.getKey()+".plantuml").toPath();
				try {
				Files.write(
						path, 
						diagram.getDefinition().getBytes(Charset.forName("UTF-8")));
				} catch(IOException e) {
					throw new CantWriteDiagram(
							String.format("Can't write diagram %s in file %s",
									diagram.getKey(), path),
							e);
				}
			});
	}

}
