package org.ndx.agile.architecture.base.enhancers.dependencies;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.ndx.agile.architecture.base.OutputBuilder;
import org.ndx.agile.architecture.base.enhancers.ModelElementAdapter;
import org.ndx.agile.architecture.base.enhancers.ModelElementKeys;

import com.structurizr.model.Element;
import com.structurizr.model.InteractionStyle;
import com.structurizr.model.StaticStructureElement;

/**
 * Under some circumstances (typically maven projects some elements may ahve
 * dependencies links added as properties of element ... This component process
 * those properties and transform these into valid agile Structurizr
 * dependencies.
 * 
 * @author Nicolas
 *
 */
public class AddDependenciesFromProperties extends ModelElementAdapter {
	@Inject
	Logger logger;

	@Override
	public int priority() {
		return TOP_PRIORITY_FOR_INTERNAL_ENHANCERS + 5;
	}

	/**
	 * If maven project declares the "good" property, content of this property is
	 * searched in C4 model and dependency is automatically added. Notice only
	 * properties of this maven module will be used, and not properties declared in
	 * parent poms.
	 * 
	 * @param element element to add dependency to
	 */
	@Override
	protected void processElement(StaticStructureElement element, OutputBuilder builder) {
		String dependencyDescription = element.getProperties().getOrDefault(ModelElementKeys.EXTERNAL_DEPENDENCY_DESCRIPTION, "");
		if (element.getProperties().containsKey(ModelElementKeys.EXTERNAL_DEPENDENCIES)) {
			String externalDependencies = element.getProperties().get(ModelElementKeys.EXTERNAL_DEPENDENCIES)
					.toString();
			String[] externalDependenciesList = externalDependencies.split(";");
			for (String external : externalDependenciesList) {
				addDependencyFromProperties(element, external, dependencyDescription);
			}
		}
	}

	private void addDependencyFromProperties(StaticStructureElement element, String dependencyName,
			String dependencyDescription) {
		// Can we find one unique dependency ?
		List<Element> matching = element.getModel().getElements().stream()
				.filter(modelElement -> modelElement.getCanonicalName().contains(dependencyName))
				.collect(Collectors.toList());
		if (matching.isEmpty()) {
			logger.warning(String.format(
					"We were looking for a dependency of %s which canonical name would contain %s, but found nothing. Please use a more signifiant part of name",
					element.getCanonicalName(), dependencyName));
		} else if (matching.size() > 1) {
			final LevenshteinDistance distance = new LevenshteinDistance();
			Element used = matching.stream()
				.sorted((first, second) -> distance.apply(dependencyName, first.getCanonicalName())-distance.apply(dependencyName, second.getCanonicalName()))
				.findFirst().get();
			logger.warning(String.format(
					"We were looking for a dependency of %s which canonical name would contain %s, but found %s. "
							+ "We use nearest according to Leveshtein distance, which is %s. If it is not good, use a longer part of the name.",
							element.getCanonicalName(), dependencyName,
							matching.stream().map(m -> m.getCanonicalName()).collect(Collectors.joining(", ")),
							used.getCanonicalName()));
			addDependencyFromProperties(element, used, dependencyDescription);
		} else {
			Element dependency = matching.get(0);
			addDependencyFromProperties(element, dependency, dependencyDescription);
		}
	}

	private void addDependencyFromProperties(StaticStructureElement element, Element dependency,
			String dependencyDescription) {
		if (dependency instanceof StaticStructureElement) {
			StaticStructureElement structureElement = (StaticStructureElement) dependency;
			element.uses(structureElement, dependencyDescription, "", InteractionStyle.Synchronous);
		}
	}
}
