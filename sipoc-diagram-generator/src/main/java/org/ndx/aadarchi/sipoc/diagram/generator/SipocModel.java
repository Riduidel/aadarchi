package org.ndx.aadarchi.sipoc.diagram.generator;

import com.structurizr.model.Element;

import javax.inject.Inject;
import java.util.Set;

/**
 * Incoming relationship : afferent relationship that are connected
 * Input : description of the incoming relationship
 * Process : description of the current element
 * Output : description off the outgoing relationship
 * Outgoing relationship : efferent relationship connected at the exit
 */
public class SipocModel {

    private Set<String> incomingRelationships;
    private Set<String> incomingRelationshipDescriptions;
    private Set<String> processDescription;
    private Set<String> outgoingRelationships;
    private Set<String> outgoingRelationshipsDescriptions;

    @Inject
    public SipocModel() {
    }

    private void buildIncomingRelationship(Element element) {
        element.getModel().getRelationships().stream()
                .filter(relationship -> relationship.getDestination().equals(element))
                .forEach(relationship -> incomingRelationships.add(relationship.getSource().getName()));
    }

    private void buildIncomingRelationshipDescriptions(Element element) {
        element.getModel().getRelationships().stream()
                .filter(relationship -> relationship.getDestination().equals(element))
                .forEach(relationship -> incomingRelationshipDescriptions.add(relationship.getSource().getDescription()));
    }

    private void buildProcessDescriptions(Element element) {
        element.getDescription();
    }

    private void buildOutgoingRelationship(Element element) {
        element.getEfferentRelationshipsWith(element);
    }

    private void buildOutgoingDescriptions(Element element) {
        element.getEfferentRelationshipsWith(element).stream()
                .filter(description -> description.getSource().equals(element))
                .forEach(description -> outgoingRelationshipsDescriptions.add(description.getDestination().getDescription()));
    }

    private String getString(Set<String> treatment) {
        return String.join("\n", treatment);
    }

    public String generateAsciidocTable() {
        return  "[cols=\"1,1,1,1,1\"]\n" + "|===\n|Incoming|Input|Process|Output|Outgoing\n\n\n\n\n" + "\n|===" +
                getString(incomingRelationships) +
                getString(incomingRelationshipDescriptions) +
                getString(processDescription) +
                getString(outgoingRelationshipsDescriptions) +
                getString(outgoingRelationships);
    }
    public void buildAsciidocTable(Element element) {
        buildIncomingRelationship(element);
        buildIncomingRelationshipDescriptions(element);
        buildProcessDescriptions(element);
        buildOutgoingRelationship(element);
        buildOutgoingDescriptions(element);
    }
}
