package com.github.riduidel.agile.architecture.documentation.system;

import java.io.IOException;

import javax.enterprise.context.ApplicationScoped;

import org.ndx.agile.architecture.base.ArchitectureModelProvider;
import org.ndx.agile.architecture.base.enhancers.ModelElementKeys;

import com.structurizr.Workspace;
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
		architect.uses(agileArchitecture, "Writes");
		stakeholder.uses(agileArchitecture, "Read");
		/////////////////////////////////////////////////////////////////////////////////////////
		
		Container archetype = agileArchitecture.addContainer("archetype", "Archetype generating a valid build", "maven archetype");
		architect.uses(archetype, "Bootstrap a valid project");

		Container maven = agileArchitecture.addContainer("maven", "Maven build tool", "Maven build tool");
		architect.uses(maven, "Generates documentation");

		Container base = agileArchitecture.addContainer("base", "Architecture base", "Java executable");
		base.addProperty(ModelElementKeys.SCM_PROJECT, "https://github.com/Riduidel/agile-architecture-documentation-system/");
		base.addProperty(ModelElementKeys.SCM_PATH, base.getName());
		
		Component architectureDocumentationBuilder = base.addComponent("ArchitectureDocumentationBuilder", "Architecture documentation builder bootstraps the process", "java/CDI");
		Component architectureModelProvider = base.addComponent("ArchitectureModelProvider", "ArchitectureModelProvider is the interface implemented by the user Architecture class");
		Component architectureEnhancer = base.addComponent("ArchitectureEnhancer", "Starts all enhancers loaded by CDI");
		
		architectureEnhancer.uses(
				base.addComponent("ImplicitIncludeManager", "Includes documentation of components in generated documentation without having to write anything superfluous", "java"), 
				"Generates incldues for the documented components");
		architectureEnhancer.uses(
				base.addComponent("SCMReadmeReader", "Read model element readme and include it in code section", "java"), 
				"Fetches README and include them");
		architectureEnhancer.uses(
				base.addComponent("SCMLinkGenerator", "Add link to SCM source for model elements", "java"), 
				"Generates lins to SCM sources");
		architectureEnhancer.uses(
				base.addComponent("DocumentCollector", "Collects all generated documents", "java"), 
				"Collects documents in generated final asciidoc");
		architectureEnhancer.uses(
				base.addComponent("GraphEmitter", "Generates all diagrams", "java"), 
				"Generates diagrams of model elements");

		maven.uses(architectureDocumentationBuilder, "Invokes that Java executable during maven build");
		architectureDocumentationBuilder.uses(architectureModelProvider, "Get initial architecture desription model");
		architectureDocumentationBuilder.uses(architectureEnhancer, "Enhances produced reports");
		
		Component gitHub = base.addComponent("github-scm-handler", "GitHub SCM Handler", "java");
		gitHub.addProperty(ModelElementKeys.SCM_PROJECT, "https://github.com/Riduidel/agile-architecture-documentation-system/");
		gitHub.addProperty(ModelElementKeys.SCM_PATH, gitHub.getName());

		Component gitLab = base.addComponent("gitlab-scm-handler", "GitLab SCM Handler", "java");
		gitLab.addProperty(ModelElementKeys.SCM_PROJECT, "https://github.com/Riduidel/agile-architecture-documentation-system/");
		gitLab.addProperty(ModelElementKeys.SCM_PATH, gitLab.getName());

		Component adrTicketsExtractor = base.addComponent("adr-tickets-extractor", "ADR Tickets Extractor", "java");
		adrTicketsExtractor.addProperty(ModelElementKeys.SCM_PROJECT, "https://github.com/Riduidel/agile-architecture-documentation-system/");
		adrTicketsExtractor.addProperty(ModelElementKeys.SCM_PATH, adrTicketsExtractor.getName());
		adrTicketsExtractor.uses(gitLab, "Read tickets from Gitlab if configured so");
		adrTicketsExtractor.uses(gitHub, "Read tickets from GitHub if configured so");
		architectureEnhancer.uses(adrTicketsExtractor, "Produces ADR reporting");

		Component cdiConfigExtension = base.addComponent("cdi-config-extension", "CDI Config extensions", "java");
		cdiConfigExtension.addProperty(ModelElementKeys.SCM_PROJECT, "https://github.com/Riduidel/agile-architecture-documentation-system/");
		cdiConfigExtension.addProperty(ModelElementKeys.SCM_PATH, cdiConfigExtension.getName());

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

}
