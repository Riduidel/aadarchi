package org.ndx.aadarchi.sipoc.diagram.generator;

import com.structurizr.model.Element;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class SipocModel {


    Set<String> buildIncomingRelationship(Element element) {
        return  element.getEfferentRelationshipsWith(element).stream()
                .map(relationship -> relationship.getSource().getName())
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    Set<String> buildIncomingRelationshipDescriptions(Element element) {
        return  element.getEfferentRelationshipsWith(element).stream()
                .map(relationship -> relationship.getSource().getDescription())
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    String buildProcessDescriptions(Element element) {
       return element.getDescription();
    }

    Set<String> buildOutgoingRelationships(Element element) {
        return element.getModel().getRelationships().stream()
                .filter(relationship -> relationship.getSource().equals(element))
                .map(relationship -> relationship.getDestination().getName())
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    Set<String> buildOutgoingRelationshipDescriptions(Element element) {
        return element.getModel().getRelationships().stream()
                .filter(description -> description.getSource().equals(element))
                .map(relationship -> relationship.getDestination().getDescription())
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private String getString(Set<String> treatment) {
        return String.join("\n", treatment);
    }


	public String generateSipocDiagram(Element element) {
        buildIncomingRelationship(element);
		buildIncomingRelationshipDescriptions(element);
		buildProcessDescriptions(element);
		buildOutgoingRelationships(element);
		buildOutgoingRelationshipDescriptions(element);

		return  "[cols=\"1,1,1,1,1\"]\n" + "|===\n|Incoming|Input|Process|Output|Outgoing\n\n\n\n\n" + "\n|===" +
			getString(buildIncomingRelationship(element)) +
			getString(buildIncomingRelationshipDescriptions(element)) +
			buildProcessDescriptions(element) +
			getString(buildOutgoingRelationships(element)) +
			getString(buildOutgoingRelationshipDescriptions(element));
    }
}
