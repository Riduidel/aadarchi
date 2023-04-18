package org.ndx.aadarchi.sipoc.diagram.generator;

import com.structurizr.model.Element;
import com.structurizr.model.Relationship;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

public class SipocModel {
	
	String describeConnectedElement(Element element) {
		return String.format(" *%s* %s", element.getName(), StringUtils.isBlank(element.getDescription()) ? "" : element.getDescription());
	}

	Set<String> buildIncomingRelationships(Element element) {
		return element.getModel().getRelationships().stream()
				.filter(relationship -> relationship.getDestination().equals(element)).map(Relationship::getSource)
				.map(this::describeConnectedElement).sorted()
				.collect(Collectors.toCollection(LinkedHashSet::new));
	}

	Set<String> buildIncomingRelationshipDescriptions(Element element) {
		return element.getModel().getRelationships().stream()
				.filter(relationship -> relationship.getDestination().equals(element)).map(Relationship::getDescription)
				.filter(Objects::nonNull).sorted().collect(Collectors.toCollection(LinkedHashSet::new));
	}

	String buildProcessDescriptions(Element element) {
		return element.getDescription();
	}

	Set<String> buildOutgoingRelationships(Element element) {
		return element.getModel().getRelationships().stream()
				.filter(relationship -> relationship.getSource().equals(element)).map(Relationship::getDestination)
				.map(this::describeConnectedElement).sorted()
				.collect(Collectors.toCollection(LinkedHashSet::new));
	}

	Set<String> buildOutgoingRelationshipDescriptions(Element element) {
		return element.getModel().getRelationships().stream()
				.filter(relationship -> relationship.getSource().equals(element)).map(Relationship::getDescription)
				.sorted().collect(Collectors.toCollection(LinkedHashSet::new));
	}

	private String getString(Set<String> treatment) {
		return String.join("\n\n", treatment);
	}

	public String generateSipocDiagram(Element element) {
		Set<String> buildIncomingRelationships = buildIncomingRelationships(element);
		Set<String> buildIncomingRelationshipDescriptions = buildIncomingRelationshipDescriptions(element);
		Set<String> buildOutgoingRelationshipDescriptions = buildOutgoingRelationshipDescriptions(element);
		Set<String> buildOutgoingRelationships = buildOutgoingRelationships(element);
		if(!buildIncomingRelationships.isEmpty() || 
				!buildIncomingRelationshipDescriptions.isEmpty() ||
				!buildOutgoingRelationshipDescriptions.isEmpty() ||
				!buildOutgoingRelationships.isEmpty()) {
			return element.getModel().getRelationships().stream()
					.map(relationship -> {
						return String.format("|%s|%s|%s|%s|%s", 
								getString(buildIncomingRelationships),
								getString(buildIncomingRelationshipDescriptions), 
								buildProcessDescriptions(element),
								getString(buildOutgoingRelationshipDescriptions),
								getString(buildOutgoingRelationships));
					})
					.distinct()
					.collect(Collectors.joining("\n\n\n\n\n",
							"[cols=\"1,1,1,1,1\"]\n" + "|===\n|Incoming|Input|Process|Output|Outgoing\n\n\n\n\n",
							"\n|==="));
		} else {
			return "";
		}
	}

}
