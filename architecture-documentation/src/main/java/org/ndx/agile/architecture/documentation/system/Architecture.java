package org.ndx.agile.architecture.documentation.system;

import java.io.File;
import java.net.URLClassLoader;
import java.util.Arrays;

import javax.enterprise.context.ApplicationScoped;

import org.ndx.agile.architecture.base.ArchitectureModelProvider;
import org.ndx.agile.architecture.base.enhancers.ModelElementKeys;
import org.ndx.agile.architecture.inferer.maven.MavenEnhancer;
import org.ndx.agile.architecture.tickets.ADRExtractor;

import com.structurizr.Workspace;
import com.structurizr.analysis.ComponentFinder;
import com.structurizr.analysis.SourceCodeComponentFinderStrategy;
import com.structurizr.analysis.StructurizrAnnotationsComponentFinderStrategy;
import com.structurizr.model.Component;
import com.structurizr.model.Container;
import com.structurizr.model.Model;
import com.structurizr.model.Person;
import com.structurizr.model.SoftwareSystem;
import com.structurizr.view.ComponentView;
import com.structurizr.view.ContainerView;
import com.structurizr.view.SystemContextView;
import com.structurizr.view.ViewSet;

@ApplicationScoped
public class Architecture implements ArchitectureModelProvider {

	/**
	 * Creates the workspace object and add in it both the architecture components
	 * AND the views used to display it
	 * 
	 * @return
	 */
	public Workspace describeArchitecture() {
		Workspace workspace = new Workspace("agile-architecture-documentation-system", "This is the model of the agile architecture documentation system.");
		Model model = workspace.getModel();

		Person architect = model.addPerson("Architect", "The architect as team scribe is the writer of this kind of documentation.");
		Person stakeholder = model.addPerson("Stakeholder", "All project stakeholders are readers of this kind of documentation.");
		SoftwareSystem agileArchitecture = model.addSoftwareSystem("Agile architecture documentation", "This software system generates the documentation.");
		agileArchitecture.addProperty(ModelElementKeys.ISSUE_MANAGER, "https://github.com/Riduidel/agile-architecture-documentation-system");
		agileArchitecture.addProperty(ADRExtractor.AGILE_ARCHITECTURE_TICKETS_ADR_LABEL, "decision");
		architect.uses(agileArchitecture, "Writes");
		stakeholder.uses(agileArchitecture, "Read");
		/////////////////////////////////////////////////////////////////////////////////////////
		
		Container archetype = agileArchitecture.addContainer("archetype", "Archetype generating a valid build", "maven archetype");
		archetype.addProperty(MavenEnhancer.AGILE_ARCHITECTURE_MAVEN_POM, locate("archetype/pom.xml"));
		architect.uses(archetype, "Bootstrap a valid project");

		Container maven = agileArchitecture.addContainer("maven", "Maven build tool", "Maven build tool");
		architect.uses(maven, "Generates documentation");

		Container base = agileArchitecture.addContainer("base", "Architecture base", "Java executable");
		base.addProperty(MavenEnhancer.AGILE_ARCHITECTURE_MAVEN_CLASS, ArchitectureModelProvider.class.getName());
		base.addProperty(ModelElementKeys.SCM_PATH, base.getName());
		
        ComponentFinder componentFinder = new ComponentFinder(
                base,
                ArchitectureModelProvider.class.getPackageName(),
                new StructurizrAnnotationsComponentFinderStrategy(),
                new SourceCodeComponentFinderStrategy(new File("../base/src/main/java"))
                
        );
        if(getClass().getClassLoader()instanceof URLClassLoader) {
        	componentFinder.setUrlClassLoader((URLClassLoader) getClass().getClassLoader());
        }
        try {
			componentFinder.findComponents();
		} catch (Exception e) {
			throw new RuntimeException("Unable to locate components in ", e);
		}
        model.addImplicitRelationships();
        // Damn, structurizr-annotations doesn't understand CDI. Let's supplement it!
        base.getComponentWithName("ArchitectureEnhancer").uses(base.getComponentWithName("DocumentsCollector"), "Collects documents in source folder");
        base.getComponentWithName("ArchitectureEnhancer").uses(base.getComponentWithName("SCMLinkGenerator"), "Generates links to SCM sources");
        base.getComponentWithName("ArchitectureEnhancer").uses(base.getComponentWithName("SCMReadmeReader"), "Includes elements readme when they exist");
        base.getComponentWithName("ArchitectureEnhancer").uses(base.getComponentWithName("ImplicitIncludeManager"), "Generates includes for all enhancers");
        base.getComponentWithName("ArchitectureEnhancer").uses(base.getComponentWithName("GraphEmitter"), "Generates diagrams in PlantUML format");

		maven.uses(base.getComponentWithName("ArchitectureDocumentationBuilder"), "Invokes that Java executable during maven build");
		
		Component gitHub = base.addComponent("github-scm-handler", "GitHub SCM Handler", "java");
		gitHub.addProperty(MavenEnhancer.AGILE_ARCHITECTURE_MAVEN_POM, locate("github-scm-handler/pom.xml"));
		gitHub.addProperty(ModelElementKeys.SCM_PATH, gitHub.getName());
		base.getComponentWithName("SCMLinkGenerator").uses(gitHub, "Get project source link");
		base.getComponentWithName("SCMReadmeReader").uses(gitHub, "Get project readme");

		Component gitLab = base.addComponent("gitlab-scm-handler", "GitLab SCM Handler", "java");
		gitLab.addProperty(MavenEnhancer.AGILE_ARCHITECTURE_MAVEN_POM, locate("gitlab-scm-handler/pom.xml"));
		gitLab.addProperty(ModelElementKeys.SCM_PATH, gitLab.getName());
		base.getComponentWithName("SCMLinkGenerator").uses(gitLab, "Get project source link");
		base.getComponentWithName("SCMReadmeReader").uses(gitLab, "Get project readme");

		Component adrTicketsExtractor = base.addComponent("adr-tickets-extractor", "enhanced by maven");
		adrTicketsExtractor.addProperty(MavenEnhancer.AGILE_ARCHITECTURE_MAVEN_CLASS, ADRExtractor.class.getName());
		adrTicketsExtractor.addProperty(ModelElementKeys.SCM_PATH, adrTicketsExtractor.getName());
		adrTicketsExtractor.uses(gitLab, "Read tickets from Gitlab if configured so");
		adrTicketsExtractor.uses(gitHub, "Read tickets from GitHub if configured so");
		base.getComponentWithName("ArchitectureEnhancer").uses(adrTicketsExtractor, "Produces ADR reporting");

		Component cdiConfigExtension = base.addComponent("cdi-config-extension", "CDI Config extensions", "java");
		cdiConfigExtension.addProperty(MavenEnhancer.AGILE_ARCHITECTURE_MAVEN_POM, locate("cdi-config-extension/pom.xml"));
		cdiConfigExtension.addProperty(ModelElementKeys.SCM_PATH, cdiConfigExtension.getName());
		base.getComponentWithName("ArchitectureDocumentationBuilder").uses(cdiConfigExtension, "Eases out some CDI code");

		Component mavenEnhancer = base.addComponent("maven-metadata-inferer", "Enhanced by Maven");
		cdiConfigExtension.addProperty(MavenEnhancer.AGILE_ARCHITECTURE_MAVEN_CLASS, MavenEnhancer.class.getName());
		cdiConfigExtension.addProperty(ModelElementKeys.SCM_PATH, mavenEnhancer.getName());
		base.getComponentWithName("ArchitectureDocumentationBuilder").uses(mavenEnhancer, "Infer most of element details from Maven infos");

		Container asciidoc = agileArchitecture.addContainer("asciidoc", "Asciidoc tooling", "Maven plugin");

		maven.uses(base, "Generates diagrams and asciidoc includes");
		maven.uses(asciidoc, "Generates documentation as usable text in HTML/PDF/...");

		/////////////////////////////////////////////////////////////////////////////////////////
		ViewSet views = workspace.getViews();
		SystemContextView contextView = views.createSystemContextView(agileArchitecture, "SystemContext",
				"Illustration of agile-architecture-documentation usage");
		contextView.addAllSoftwareSystems();
		contextView.addAllPeople();

		ContainerView agileArchitectureContainers = views.createContainerView(agileArchitecture, "agile.architecture.containers", "Agile architecture containers");
		agileArchitectureContainers.addAllContainersAndInfluencers();
		
		ComponentView agileArchitectureBaseComponents = views.createComponentView(base, "agile.architecture.base.components", "Agile architecture base components view");
		agileArchitectureBaseComponents.addAllComponents();
		
//		Styles styles = views.getConfiguration().getStyles();
//		styles.addElementStyle(Tags.SOFTWARE_SYSTEM).background("#1168bd").color("#ffffff");
//		styles.addElementStyle(Tags.PERSON).background("#08427b").color("#ffffff").shape(Shape.Person);
		return workspace;
	}

	private String locate(String string) {
		return locateFileBelowPath(string).toURI().toString();
	}

	private File locateFileBelowPath(String path) {
		String[] fragments = path.split("/");
		for(File potential : Arrays.asList(new File("."), new File(".."))) {
			File tested = potential;
			for(String fragment : fragments) {
				tested = new File(tested, fragment);
				if(!tested.exists())
					break;
			}
			if(tested.exists()) {
				return tested;
			}
		}
		throw new UnsupportedOperationException(String.format("We were unable to locate path %s", path));
	}

}
