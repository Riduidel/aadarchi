package org.ndx.aadarchi.base.enhancers.graph;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.ndx.aadarchi.base.OutputBuilder;
import org.ndx.aadarchi.base.ViewEnhancer;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys.ConfigProperties.DiagramsDir;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys.ConfigProperties.Force;

import com.structurizr.Workspace;
import com.structurizr.annotation.Component;
import com.structurizr.io.plantuml.C4PlantUMLWriter;
import com.structurizr.io.plantuml.C4PlantUMLWriter.Layout;
import com.structurizr.io.plantuml.PlantUMLWriter;
import com.structurizr.view.ComponentView;
import com.structurizr.view.ContainerView;
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
	File destination;
	@Inject public void setDestination(@ConfigProperty(name = DiagramsDir.NAME, defaultValue = DiagramsDir.VALUE) File destination) {
		this.destination = destination.getAbsoluteFile();
	}
	@Inject @ConfigProperty(name = Force.NAME, defaultValue = Force.VALUE) boolean force;

	@Inject 
	@ConfigProperty(name = ModelElementKeys.PREFIX+"diagrams.layout", defaultValue = "LAYOUT_WITH_LEGEND")
	String layoutMode;

	@Inject 
	@ConfigProperty(name = ModelElementKeys.PREFIX+"diagrams.plantuml.pencils", defaultValue = "https://github.com/plantuml-stdlib/C4-PlantUML")
	String plantumlPencils;
	
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
	public boolean startVisit(View s) { return true; }

	/**
	 * At view end visit, we selectively remove the layout information if it is layout with legend
	 */
	@Override
	public void endVisit(View diagram, OutputBuilder builder) {
		if(diagram instanceof ComponentView || diagram instanceof ContainerView) {
			Path path = new File(destination, diagram.getKey()+".plantuml").toPath();
			try {
				String diagramText = Files.readString(path);
				if(diagramText.contains(C4PlantUMLWriter.Layout.LAYOUT_WITH_LEGEND.name())) {
					diagramText = diagramText.replace(C4PlantUMLWriter.Layout.LAYOUT_WITH_LEGEND.name()+"()", "");
					Files.writeString(path, diagramText);
				}
			} catch(IOException e) {
				// Nothing to do
			}
		}
	}

	@Override
	public void endVisit(ViewSet viewset, OutputBuilder builder) {
		logger.info(String.format("All views should have been output to %s", destination.getAbsolutePath()));
	}

	@Override
	public boolean startVisit(Workspace workspace, OutputBuilder builder) {
		writeAllViews(workspace);
		return true;
	}

	@Override
	public void endVisit(Workspace workspace, OutputBuilder builder) {
	}

	private void writeAllViews(Workspace workspace) {
		Layout layout = C4PlantUMLWriter.Layout.valueOf(layoutMode);
		PlantUMLWriter plantUMLWriter = new C4PlantUMLWriter(layout, plantumlPencils);
		// Hints to have arrows more easily visible
		plantUMLWriter.addSkinParam("pathHoverColor", "GreenYellow");
		plantUMLWriter.addSkinParam("ArrowThickness", "3");
		plantUMLWriter.addSkinParam("svgLinkTarget", "_parent");
		
		destination.mkdirs();
		plantUMLWriter.toPlantUMLDiagrams(workspace).stream().parallel()
			.forEach(diagram -> {
				// Incredibly enough, that's not a view!
				Path path = new File(destination, diagram.getKey()+".plantuml").toPath();
				try {
					Files.write(
							path, 
							diagram.getDefinition().getBytes(Charset.forName("UTF-8")));
					logger.info(String.format("Generated diagram %s in file %s", diagram.getKey(), path));
				} catch(IOException e) {
					throw new CantWriteDiagram(
							String.format("Can't write diagram %s in file %s",
									diagram.getKey(), path),
							e);
				}
			});
	}

}
