package org.ndx.agile.architecture.base.enhancers;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.ndx.agile.architecture.base.AgileArchitectureException;
import org.ndx.agile.architecture.base.ModelEnhancer;
import org.ndx.agile.architecture.base.OutputBuilder;
import org.ndx.agile.architecture.base.ViewEnhancer;

import com.structurizr.Workspace;
import com.structurizr.model.Component;
import com.structurizr.model.Container;
import com.structurizr.model.Element;
import com.structurizr.model.Model;
import com.structurizr.model.Person;
import com.structurizr.model.Relationship;
import com.structurizr.model.SoftwareSystem;
import com.structurizr.view.ComponentView;
import com.structurizr.view.ContainerView;
import com.structurizr.view.CustomView;
import com.structurizr.view.DeploymentView;
import com.structurizr.view.DynamicView;
import com.structurizr.view.SystemContextView;
import com.structurizr.view.SystemLandscapeView;
import com.structurizr.view.View;
import com.structurizr.view.ViewSet;

/**
 * Enhancer that generate a structurizr dsl file The dsl file is used to
 * generate structurizr diagrams of the project architecture This dsl file will
 * be located in target/structurizr/workspace.dsl
 * 
 * @author jason-sycz feat nicolas-delsaux
 *
 */

public class ToDsl implements ModelEnhancer, ViewEnhancer {
	public static class UnableToBuildDslException extends AgileArchitectureException {

		public UnableToBuildDslException(String message, Throwable cause) {
			super(message, cause);
		}

	}

	String architecture = null;
	String relations = "";
	
	@Inject Logger logger;

	@Inject
	@ConfigProperty(name = ModelElementKeys.PREFIX + "todsl.enabled", defaultValue = "false")
	boolean toDslEnabled;
	@Inject
	@ConfigProperty(name = ModelElementKeys.PREFIX + "todsl.target", defaultValue = "${project.basedir}/target/structurizr/workspace.dsl")
	private File dslTargetFile;

	@Override
	public boolean isParallel() {
		return false;
	}

	@Override
	public int priority() {
		return Integer.MAX_VALUE - 1;
	}

	/**
	 * Convert this set of properties into Structurizr DSL properties
	 * @param indentCount
	 * @param properties
	 */
	private String propertiesToDsl(int indentCount, Map<String, String> properties) {
		if(properties.isEmpty())
			return "";
		String tabs = StringUtils.repeat('\t', indentCount);
		return properties.entrySet().stream()
				.map(entry -> String.format("%s\"%s\" \"%s\"", tabs+"\t", entry.getKey(), entry.getValue()))
				.collect(Collectors.joining("\n", "\n"+tabs+"properties {\n", "\n"+tabs+"}\n"));
	}

	@Override
	public boolean startVisit(Workspace workspace, OutputBuilder builder) {
		if (toDslEnabled) {
			architecture = String.format("workspace \"%s\" {\n", workspace.getName());
			architecture+= propertiesToDsl(1, workspace.getProperties());
		}
		return toDslEnabled;
	}

	/**
	 * Convert this set of properties into Structurizr DSL properties
	 * @param indentCount
	 * @param properties
	 */
	private String propertiesToDsl(int indentCount, Map<String, String> properties) {
		if(properties.isEmpty())
			return "";
		String tabs = StringUtils.repeat('\t', indentCount);
		return properties.entrySet().stream()
				.map(entry -> String.format("%s%s %s", tabs+"\t", entry.getKey(), entry.getValue()))
				.collect(Collectors.joining("\n", "\n"+tabs+"properties {\n", "\n"+tabs+"}\n"));
	}

	@Override
	public boolean startVisit(Model model) {
		architecture = architecture + "\tmodel {\n";
		if (!model.getPeople().isEmpty()) {
			architecture += personSetToDsl(model.getPeople());
		}
		if (!model.getRelationships().isEmpty()) {
			relations += relationshipSetToDsl(model.getRelationships());
		}
		return true;
	}

	@Override
	public boolean startVisit(SoftwareSystem softwareSystem) {
		architecture += String.format("\t\t%s = softwareSystem \"%s\" {", asVariableName(softwareSystem),
				softwareSystem.getName());
		architecture += propertiesToDsl(3, softwareSystem.getProperties());
		if (!softwareSystem.getRelationships().isEmpty()) {
			relations += relationshipSetToDsl(softwareSystem.getRelationships());
		}
		return true;
	}

	@Override
	public boolean startVisit(Container container) {
		architecture += String.format("\t\t\t%s = container \"%s\" {", asVariableName(container),
				container.getName());
		architecture += propertiesToDsl(4, container.getProperties());
		if (!container.getRelationships().isEmpty()) {
			relations += relationshipSetToDsl(container.getRelationships());
		}
		return true;
	}

	@Override
	public boolean startVisit(Component component) {
		architecture += String.format("\t\t\t\t%s = component \"%s\" {", asVariableName(component),
				component.getName());
		architecture += propertiesToDsl(5, component.getProperties());
		if (!component.getRelationships().isEmpty()) {
			relations += relationshipSetToDsl(component.getRelationships());
		}
		return true;
	}

	@Override
	public void endVisit(Component component, OutputBuilder builder) {
		architecture += "\t\t\t\t}\n";
	}

	@Override
	public void endVisit(Container container, OutputBuilder builder) {
		architecture += "\t\t\t}\n";
	}

	@Override
	public void endVisit(SoftwareSystem softwareSystem, OutputBuilder builder) {
		architecture += "\t\t}\n";
	}

	@Override
	public void endVisit(Model model, OutputBuilder builder) {
		architecture += relations + "\t}\n";
	}

	@Override
	public void endVisit(Workspace workspace, OutputBuilder outputBuilder) {
		if (toDslEnabled) {
			architecture += "}";
			try {
				StringBuilder builder = new StringBuilder(architecture);
				dslTargetFile.getParentFile().mkdirs();
				FileUtils.write(dslTargetFile, builder, "UTF-8");
			} catch (IOException e) {
				throw new UnableToBuildDslException(
						String.format("Unable to build dsl file %s", dslTargetFile.getAbsolutePath()), e);
			}
		}
	}

	private String personSetToDsl(Set<Person> person) {
		return person.stream().map(this::personToDsl).collect(Collectors.joining("\n", "", "\n"));
	}

	private String personToDsl(Person person) {
		String description = "";
		if (person.getDescription() != null)
			description = person.getDescription();
		String personText = String.format("\t\t%s = person \"%s\" \"%s\"", Objects.requireNonNull(asVariableName(person)),
				Objects.requireNonNull(person.getName()), description);
		if(!person.getProperties().isEmpty()) {
			personText += " {\n";
			personText += propertiesToDsl(3, person.getProperties());
			personText += "\t\t}";
		}
		return personText;
	}

	private String relationshipSetToDsl(Set<Relationship> relationships) {
		return relationships.stream().map(this::relationshipToDsl).collect(Collectors.joining("\n", "", "\n"));
	}

	private String relationshipToDsl(Relationship relationship) {
		String description = "";
		if (relationship.getDescription() != null)
			description = relationship.getDescription();
		String technology = "";
		if (relationship.getTechnology() != null)
			technology = relationship.getTechnology();
		String relationshipText = String.format("\t\t%s -> %s \"%s\" \"%s\"",
				asVariableName(Objects.requireNonNull(relationship.getSource())),
				asVariableName(Objects.requireNonNull(relationship.getDestination())), description, technology);
		if(!relationship.getProperties().isEmpty()) {
			relationshipText += " {\n";
			relationshipText += propertiesToDsl(3, relationship.getProperties());
			relationshipText += "}";
		}
		return relationshipText;
	}

	private String asVariableName(Element element) {
		return String.format("%s_%s", element.getName().replaceAll("[^A-Za-z0-9]", "_"), element.getId());
	}

	@Override
	public boolean startVisit(ViewSet viewset) {
		if (toDslEnabled) {
			try {
				String viewsDeclaration = IOUtils.toString(getClass().getResourceAsStream("ToDSL.default.views.dsl"),
						"UTF-8");
				architecture += "\tviews {\n";
				architecture += viewsDeclaration;
			} catch (IOException e) {
				throw new UnableToBuildDslException("Unable to read pseudo-standard view header file", e);
			}
		}
		return toDslEnabled;
	}

	@Override
	public boolean startVisit(View s) {
		return true;
	}

	@Override
	public void endVisit(View s, OutputBuilder builder) {
		String PLACEHOLDER = "\t\t\tWARNING: Include and autolayout can't be infered from static view content, so you'll have to set that yourself\n\t\t}\n";
		if(s instanceof ComponentView) {
			ComponentView c = (ComponentView) s;
			architecture += String.format("\t\tcomponent \"%s\" \"%s\" \"%s\" {\n"+PLACEHOLDER, 
					asVariableName(c.getContainer()),
					c.getKey(),
					c.getDescription());
		} else if(s instanceof ContainerView) {
			ContainerView c = (ContainerView) s;
			architecture += String.format("\t\tcontainer \"%s\" \"%s\" \"%s\" {\n"+PLACEHOLDER, 
					asVariableName(c.getSoftwareSystem()),
					c.getKey(),
					c.getDescription());
		} else if(s instanceof SystemContextView) {
			SystemContextView c = (SystemContextView) s;
			architecture += String.format("\t\tsystemContext \"%s\" \"%s\" \"%s\" {\n"+PLACEHOLDER, 
					asVariableName(c.getSoftwareSystem()),
					c.getKey(),
					c.getDescription());
		} else if(s instanceof SystemLandscapeView) {
			SystemLandscapeView c = (SystemLandscapeView) s;
			architecture += String.format("\t\tsystemLandscape \"%s\" \"%s\" {\n"+PLACEHOLDER, 
					c.getKey(),
					c.getDescription());
		} else {
			logger.warning(String.format("View to DSL isn't yet supported for given view, so view %s is ignored", s));
		}
	}

	@Override
	public void endVisit(ViewSet viewset, OutputBuilder builder) {
		architecture += "\t\t}\n";
	}
}
