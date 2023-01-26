package org.ndx.aadarchi.sipoc.diagram.generator;

import com.structurizr.model.*;
import org.ndx.aadarchi.base.AgileArchitectureSection;
import org.ndx.aadarchi.base.OutputBuilder;
import org.ndx.aadarchi.base.enhancers.ModelElementAdapter;

import java.util.*;
import java.util.stream.Collectors;


public class SipocEnhancer extends ModelElementAdapter {

    @Override
    public boolean isParallel() {
        return true;
    }

    @Override
    public int priority() {
        return TOP_PRIORITY_FOR_INTERNAL_ENHANCERS;
    }

    @Override
    public boolean startVisit(SoftwareSystem softwareSystem) {
        return true;
    }

    @Override
    public boolean startVisit(Container container) {
        return true;
    }

    @Override
    public boolean startVisit(Component component) {
        return true;
    }


    @Override
    public void endVisit(Component c, OutputBuilder builder) {
        String sipocDiagram = sipocDiagramGenerator(c);
        builder.writeToOutput(AgileArchitectureSection.code, c,this, OutputBuilder.Format.adoc, sipocDiagram);
    }

    @Override
    public void endVisit(Container c, OutputBuilder builder) {
    String sipocDiagram = sipocDiagramGenerator(c);
    builder.writeToOutput(AgileArchitectureSection.code, c,this, OutputBuilder.Format.adoc, sipocDiagram);
    }

    @Override
    public void endVisit(SoftwareSystem softwareSystem, OutputBuilder builder) {
        String sipocDiagram = sipocDiagramGenerator(softwareSystem);
        builder.writeToOutput(AgileArchitectureSection.code, softwareSystem,this, OutputBuilder.Format.adoc, sipocDiagram);
    }

    /**
     * Incoming relationship : afferent relationship that are connected
     * Input : description of the incoming relationship
     * Process : description of the current element
     * Output : description off the outgoing relationship
     * Outgoing relationship : efferent relationship connected at the exit
     */
    String sipocDiagramGenerator(Element element) {
        List<String> outcomingRelationshipsDescription =  element.getEfferentRelationshipsWith(element).stream()
                .filter(relationship -> relationship.getDestination().equals(element))
                .map(relationship -> relationship.getSource().getDescription())
                .collect(Collectors.toList());

        return  element.getModel().getRelationships().stream()
                .map(s -> String.format("|%s|%s|%s|%s|%s", getAfferentRelationshipsWith(element).keySet(), getAfferentRelationshipsWith(element).values(), element.getDescription(), outcomingRelationshipsDescription, (element.getEfferentRelationshipsWith(element))))
                .collect(Collectors.joining("\n\n\n\n\n", "[cols=\"1,1,1,1,1\"]\n" + "|===\n|Incoming|Input|Process|Output|Outgoing\n\n\n\n\n", "\n|==="));
    }

    public HashMap<String, String> getAfferentRelationshipsWith(Element element) {
        HashMap<String, String> map = new HashMap<>();
        Set<Relationship> relationships = element.getModel().getRelationships();
        for (Relationship relationship : relationships) {
            if (relationship.getSource().equals(element)) {
                map.put(relationship.getDestination().getName(), relationship.getDestination().getDescription());
            }
        }
        return map;
    }
}
