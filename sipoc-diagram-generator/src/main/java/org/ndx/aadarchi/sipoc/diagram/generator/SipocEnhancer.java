package org.ndx.aadarchi.sipoc.diagram.generator;

import com.structurizr.model.*;
import org.ndx.aadarchi.base.AgileArchitectureSection;
import org.ndx.aadarchi.base.OutputBuilder;
import org.ndx.aadarchi.base.enhancers.ModelElementAdapter;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
@Default
public class SipocEnhancer extends ModelElementAdapter {

    @Override
    public boolean isParallel() {
        return true;
    }

    @Override
    public int priority() {
        return 1;
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
        String sipocDiagram = sipocDiagramGenerator(c);
        builder.writeToOutput(AgileArchitectureSection.code, softwareSystem,this, OutputBuilder.Format.adoc, sipocDiagram);
    }

    String sipocDiagramGenerator(Element element) {
    	// In the whole model
        return element.getModel().getRelationships().stream()
        	// Get relationships where element is source or destination
        	.filter(relationship -> relationship.getSource().equals(element) || relationship.getDestination().equals(element))
        	// Convert relationship to text line
            .map(element -> String.format("%s,%s,%s",relationshipSource,  element.getDescription(),element.getRelationships()))
            // Aggregate relationships ina  table
            .collect(Collectors.joining("\n\n", "[cols=\"1,1,1\"]\n" + "|\n|Incoming Relationship|Process|Outgoing Relationship\n\n", "\n|"));
    }
}
