package org.ndx.aadarchi.sipoc.diagram.generator;

import com.structurizr.model.*;
import org.ndx.aadarchi.base.AgileArchitectureSection;
import org.ndx.aadarchi.base.OutputBuilder;
import org.ndx.aadarchi.base.enhancers.ModelElementAdapter;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;


@Default
@ApplicationScoped
public class SipocEnhancer extends ModelElementAdapter {

    SipocModel sipocModel;

    @Override
    public boolean isParallel() {
        return true;
    }

    @Override
    public int priority() {
        return TOP_PRIORITY_FOR_INTERNAL_ENHANCERS - 1;
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
        String sipocDiagram = generateSipocDiagram(c);
        builder.writeToOutput(AgileArchitectureSection.code, c,this, OutputBuilder.Format.adoc, sipocDiagram);
    }

    @Override
    public void endVisit(Container c, OutputBuilder builder) {
    String sipocDiagram = generateSipocDiagram(c);
    builder.writeToOutput(AgileArchitectureSection.code, c,this, OutputBuilder.Format.adoc, sipocDiagram);
    }

    @Override
    public void endVisit(SoftwareSystem softwareSystem, OutputBuilder builder) {
        String sipocDiagram = generateSipocDiagram(softwareSystem);
        builder.writeToOutput(AgileArchitectureSection.code, softwareSystem,this, OutputBuilder.Format.adoc, sipocDiagram);
    }

    String generateSipocDiagram(Element element) {
        SipocModel sipocModel = new SipocModel();
        return sipocModel.generateSipocDiagram(element);
    }
}
