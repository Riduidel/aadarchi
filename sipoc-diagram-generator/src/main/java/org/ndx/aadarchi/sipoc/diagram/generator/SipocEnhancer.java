package org.ndx.aadarchi.sipoc.diagram.generator;

import com.structurizr.model.*;
import org.ndx.aadarchi.base.AgileArchitectureSection;
import org.ndx.aadarchi.base.OutputBuilder;
import org.ndx.aadarchi.base.enhancers.ModelElementAdapter;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SipocEnhancer extends ModelElementAdapter {

    @Inject
    Instance<Element> elements;

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
        String sipocDiagram = sipocDiagramGenerator(elements.stream());
        builder.writeToOutput(AgileArchitectureSection.code, c,this, OutputBuilder.Format.adoc, sipocDiagram);
    }

    @Override
    public void endVisit(Container c, OutputBuilder builder) {
    String sipocDiagram = sipocDiagramGenerator(elements.stream());
    builder.writeToOutput(AgileArchitectureSection.code, c,this, OutputBuilder.Format.adoc, sipocDiagram);
    }

    @Override
    public void endVisit(SoftwareSystem softwareSystem, OutputBuilder builder) {
        String sipocDiagram = sipocDiagramGenerator(elements.stream());
        builder.writeToOutput(AgileArchitectureSection.code, softwareSystem,this, OutputBuilder.Format.adoc, sipocDiagram);
    }

    String sipocDiagramGenerator(Stream<Element> stream) {
        Set<Relationship> relationshipsSource = elements.get().getModel().getRelationships();
        relationshipsSource.stream().filter(relationship -> relationship.getDestination().equals(elements))
                .collect(Collectors.toList());
        return stream.filter(element -> element.getModel().getRelationships().contains(element))
                .map(element -> String.format("%s,%s,%s",relationshipsSource, element.getDescription(),element.getRelationships()))
                .collect(Collectors.joining("\n\n", "[cols=\"1,1,1,1,1\"]\n" + "|\n|Incoming Relationship|Input|Process|Output|Outgoing Relationship\n\n", "\n|"));
    }
}
