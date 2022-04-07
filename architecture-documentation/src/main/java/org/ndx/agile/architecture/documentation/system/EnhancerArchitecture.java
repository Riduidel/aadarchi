package org.ndx.agile.architecture.documentation.system;


import com.structurizr.model.Component;
import com.structurizr.model.Container;
import com.structurizr.model.SoftwareSystem;
import org.apache.commons.io.FileUtils;
import org.ndx.agile.architecture.base.AgileArchitectureSection;
import org.ndx.agile.architecture.base.Enhancer;
import org.ndx.agile.architecture.base.OutputBuilder;
import org.ndx.agile.architecture.base.enhancers.ModelElementAdapter;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EnhancerArchitecture extends ModelElementAdapter {

    @Inject
    Instance<Enhancer> enhancerInstance;

    @Override
    public int priority() {
        return 999999;
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
    public void endVisit(SoftwareSystem softwareSystem, OutputBuilder builder) {
        String architecture = toArchitectureModel(enhancerInstance.stream());
        File output = builder.outputFor(AgileArchitectureSection.code, softwareSystem, this, "dsl");
        try {
            FileUtils.write(output, architecture, "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException("Can't believe I can't write the file " + output.getAbsolutePath(), e);
        }
    }

    String toArchitectureModel(Stream<Enhancer> stream) {
        return null;
    }

}
