package org.ndx.aadarchi.base.enhancers.graph;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.ndx.aadarchi.base.OutputBuilder;
import org.ndx.aadarchi.base.ViewEnhancer;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys.ConfigProperties.DiagramsDir;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys.ConfigProperties.Force;
import org.ndx.aadarchi.base.utils.CantToResolvePath;

import com.structurizr.Workspace;
import com.structurizr.annotation.Component;
import com.structurizr.export.plantuml.C4PlantUMLExporter;
import com.structurizr.view.View;
import com.structurizr.view.ViewSet;

/**
 * Generates all graph and output them in the {@link #destination} folder
 * @author nicolas-delsaux
 *
 */
@Component(technology = "Java, CDI")
@ApplicationScoped
public class GraphEmitter implements ViewEnhancer {
	@Inject Logger logger;
	@Inject @ConfigProperty(name = DiagramsDir.NAME, defaultValue = DiagramsDir.VALUE) FileObject destination;

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

	@Override
	public void endVisit(View diagram, OutputBuilder builder) {
		// No more used
	}

	@Override
	public void endVisit(ViewSet viewset, OutputBuilder builder) {
		logger.info(String.format("All views should have been output to %s", destination));
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
		C4PlantUMLExporter plantUMLWriter = new C4PlantUMLExporter();
		// Hints to have arrows more easily visible
		plantUMLWriter.addSkinParam("pathHoverColor", "GreenYellow");
		plantUMLWriter.addSkinParam("ArrowThickness", "3");
		plantUMLWriter.addSkinParam("svgLinkTarget", "_parent");
		
		try {
			destination.createFolder();
		} catch (FileSystemException e1) {
			throw new CantCreateFolder(String.format("Can't create folder %s", destination), e1);
		}
		
		plantUMLWriter.export(workspace).stream().parallel()
			.forEach(diagram -> {
				// Incredibly enough, that's not a view!
				String name = diagram.getKey()+".plantuml";
				FileObject output;
				try {
					output = destination.resolveFile(name);
					try {
						IOUtils.write(diagram.getDefinition().getBytes(Charset.forName("UTF-8")),
								output.getContent().getOutputStream());
						logger.info(String.format("Generated diagram %s in file %s", diagram.getKey(), output));
					} catch(IOException e) {
						throw new CantWriteDiagram(
								String.format("Can't write diagram %s in file %s",
										diagram.getKey(), output),
								e);
					}
				} catch (FileSystemException e1) {
					throw new CantToResolvePath(String.format("Unable to resolve path to %s/%s", destination, name), e1);
				}
			});
	}

}
