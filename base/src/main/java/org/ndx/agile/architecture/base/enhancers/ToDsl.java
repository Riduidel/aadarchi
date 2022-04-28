package org.ndx.agile.architecture.base.enhancers;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.structurizr.model.*;
import com.structurizr.view.View;
import com.structurizr.view.ViewSet;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.ndx.agile.architecture.base.AgileArchitectureException;
import org.ndx.agile.architecture.base.ModelEnhancer;
import org.ndx.agile.architecture.base.OutputBuilder;
import org.ndx.agile.architecture.base.ViewEnhancer;

import com.structurizr.Workspace;

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

	@Inject
	@ConfigProperty(name = ModelElementKeys.PREFIX + "enhancements")
	File enhancementsBase;
	@Inject
	@ConfigProperty(name = ModelElementKeys.PREFIX + "todsl.enabled", defaultValue = "false")
	boolean toDslEnabled;

	@Override
	public boolean isParallel() {
		return false;
	}

	@Override
	public int priority() {
		return Integer.MAX_VALUE - 1;
	}

	@Override
	public boolean startVisit(Workspace workspace, OutputBuilder builder) {
		if (toDslEnabled) {
			architecture = String.format("workspace \"%s\" {\n\n", workspace.getName());
		}
		return toDslEnabled;
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
		architecture += String.format("\t\t%s = softwareSystem \"%s\" {\n", asVariableName(softwareSystem),
				softwareSystem.getName());
		if (!softwareSystem.getRelationships().isEmpty()) {
			relations += relationshipSetToDsl(softwareSystem.getRelationships());
		}
		return true;
	}

	@Override
	public boolean startVisit(Container container) {
		architecture += String.format("\t\t\t%s = container \"%s\" {\n", asVariableName(container),
				container.getName());
		if (!container.getRelationships().isEmpty()) {
			relations += relationshipSetToDsl(container.getRelationships());
		}
		return true;
	}

	@Override
	public boolean startVisit(Component component) {
		architecture += String.format("\t\t\t\t%s = component \"%s\" {\n", asVariableName(component),
				component.getName());
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
			File target = new File(enhancementsBase.getParentFile(), "workspace.dsl");
			try {
				StringBuilder builder = new StringBuilder(architecture);
				FileUtils.write(target, builder, "UTF-8");
			} catch (IOException e) {
				throw new UnableToBuildDslException(
						String.format("Unable to build dsl file %s", target.getAbsolutePath()), e);
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
		return String.format("\t\t%s = person \"%s\" \"%s\"", Objects.requireNonNull(asVariableName(person)),
				Objects.requireNonNull(person.getName()), description);
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
		return String.format("\t\t%s -> %s \"%s\" \"%s\"",
				asVariableName(Objects.requireNonNull(relationship.getSource())),
				asVariableName(Objects.requireNonNull(relationship.getDestination())), description, technology);
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
				architecture += "\t\tviews {\n";
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
		throw new UnsupportedOperationException("TODO implement view automated writing");
	}

	@Override
	public void endVisit(ViewSet viewset, OutputBuilder builder) {
		architecture += "\t\t}\n";
	}
}
