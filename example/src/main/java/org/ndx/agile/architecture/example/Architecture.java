package org.ndx.agile.architecture.example;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Optional;
import java.util.logging.Logger;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.FileConverter;
import com.structurizr.Workspace;
import com.structurizr.io.plantuml.C4PlantUMLWriter;
import com.structurizr.io.plantuml.PlantUMLDiagram;
import com.structurizr.io.plantuml.PlantUMLWriter;
import com.structurizr.model.Model;
import com.structurizr.model.Person;
import com.structurizr.model.SoftwareSystem;
import com.structurizr.view.SystemContextView;
import com.structurizr.view.ViewSet;

public class Architecture {
	Logger logger = Logger.getLogger(Architecture.class.getName());

	@Parameter(names = "--destination", converter = FileConverter.class)
	File destination;

	@Parameter(names="--force", description="When set, this option forces files to be overwritten")
	private boolean force;

	@Parameter(names = "--maven-source-dir", converter = FileConverter.class, required = true, description = "Path where maven stores the source files")
	public File mavenSourceDir;
	
	@Parameter(names = "--help", help = true)
	private boolean help;

	public static void main(String[] args) throws IOException {
		Architecture main = new Architecture();
		JCommander parser = JCommander.newBuilder().addObject(main).build();
		parser.parse(args);
		if(main.help) {
			parser.usage();
		}
		main.run();
	}

	public void run() throws IOException {
		// Unless force is set we will only generate architecture info if the current class source file
		// is more recent than generated class
		File source = new File(mavenSourceDir, getClass().getName().replace('.', '/')+".java");
		if(destination.exists()) {
			// Compare with the first generated diagram
			Optional<Long> oldestDiagramDate = Arrays.asList(destination.listFiles()).stream()
				.map(file -> file.lastModified())
				.sorted()
				.findFirst();
			if(oldestDiagramDate.isPresent()) {
				// Let's consider things can be slow some times, no ?
				// In other words, if I run a slow maven build
				if(oldestDiagramDate.get()>source.lastModified()) {
					if(!force) {
						logger.warning(String.format("Oldest diagram is more recent than architecture source file. Using cached architecture diagrams"));
						return;
					}
				}
			}
		}
		logger.info(String.format("We should write output to %s", destination.getAbsolutePath()));
		Workspace workspace = describeArchitecture();

		StringWriter stringWriter = new StringWriter();
		PlantUMLWriter plantUMLWriter = new C4PlantUMLWriter();
		// Hints to have arrows more easily visible
		plantUMLWriter.addSkinParam("pathHoverColor", "GreenYellow");
		plantUMLWriter.addSkinParam("ArrowThickness", "3");
		plantUMLWriter.addSkinParam("svgLinkTarget", "_parent");
		
		destination.mkdirs();
		for(PlantUMLDiagram diagram : plantUMLWriter.toPlantUMLDiagrams(workspace)) {
			Files.write(new File(destination, diagram.getKey()+".plantuml").toPath(), diagram.getDefinition().getBytes(Charset.forName("UTF-8")));
		}
		logger.info(String.format("All views should have been output to %s", destination.getAbsolutePath()));
	}

	/**
	 * Creates the workspace object and add in it both the architecture components
	 * AND the views used to display it
	 * 
	 * @return
	 */
	private static Workspace describeArchitecture() {
		Workspace workspace = new Workspace("Getting Started", "This is a model of my software system.");
		Model model = workspace.getModel();

		Person user = model.addPerson("User", "A user of my software system.");
		SoftwareSystem softwareSystem = model.addSoftwareSystem("Software System", "My software system.");
		user.uses(softwareSystem, "Uses");

		ViewSet views = workspace.getViews();
		SystemContextView contextView = views.createSystemContextView(softwareSystem, "SystemContext",
				"An example of a System Context diagram.");
		contextView.addAllSoftwareSystems();
		contextView.addAllPeople();

//		Styles styles = views.getConfiguration().getStyles();
//		styles.addElementStyle(Tags.SOFTWARE_SYSTEM).background("#1168bd").color("#ffffff");
//		styles.addElementStyle(Tags.PERSON).background("#08427b").color("#ffffff").shape(Shape.Person);
		return workspace;
	}

}
