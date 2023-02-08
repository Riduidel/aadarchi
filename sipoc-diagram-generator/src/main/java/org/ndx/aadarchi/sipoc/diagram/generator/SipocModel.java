package org.ndx.aadarchi.sipoc.diagram.generator;

import com.structurizr.model.Element;

import javax.inject.Inject;
import java.util.Set;
import java.util.stream.Collectors;

public class SipocModel {
    /**
     * Input : description of the incoming relationship
     */
    private Set<String> incomingRelationshipDescriptions;
    /**
     * Process : description of the current element
     */
    private Set<String> processDescription;
    /**
     * Output : description off the outgoing relationship
     */
    private Set<String> outgoingRelationships;
    /**
     * Outgoing relationship : efferent relationship connected at the exit
     */
    private Set<String> outgoingRelationshipsDescriptions;

    Set<String> buildIncomingRelationship(Element element) {
        return element.getModel().getRelationships().stream()
                .filter(relationship -> relationship.getDestination().equals(element))
                .map(relationship -> relationship.getSource().getName())
                .filter(text -> text!=null)
                .collect(Collectors.toSet());
    }

    void buildIncomingRelationshipDescriptions(Element element) {
        element.getModel().getRelationships().stream()
                .filter(relationship -> relationship.getDestination().equals(element))
                .forEach(relationship -> incomingRelationshipDescriptions.add(relationship.getSource().getDescription()));
    }

    void buildProcessDescriptions(Element element) {
        element.getDescription();
    }

    void buildOutgoingRelationship(Element element) {
        element.getEfferentRelationshipsWith(element);
    }

    void buildOutgoingDescriptions(Element element) {
        element.getEfferentRelationshipsWith(element).stream()
                .filter(description -> description.getSource().equals(element))
                .forEach(description -> outgoingRelationshipsDescriptions.add(description.getDestination().getDescription()));
    }

    private String getString(Set<String> treatment) {
        return String.join("\n", treatment);
    }


	public String generateSipocDiagram(Element element) {
		buildIncomingRelationshipDescriptions(element);
		buildProcessDescriptions(element);
		buildOutgoingRelationship(element);
		buildOutgoingDescriptions(element);

		return  "[cols=\"1,1,1,1,1\"]\n" + "|===\n|Incoming|Input|Process|Output|Outgoing\n\n\n\n\n" + "\n|===" +
			getString(buildIncomingRelationship(element)) +
			getString(incomingRelationshipDescriptions) +
			getString(processDescription) +
			getString(outgoingRelationshipsDescriptions) +
			getString(outgoingRelationships);
    }
}
