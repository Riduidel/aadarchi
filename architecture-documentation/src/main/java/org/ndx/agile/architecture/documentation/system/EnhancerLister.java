package org.ndx.agile.architecture.documentation.system;

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
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EnhancerLister extends ModelElementAdapter {
    @Inject
    Logger logger;

    @Inject
    Instance<Enhancer> enhancerInstance;

    @Override
    public int priority() {
        return 1;
    }

    @Override
    public boolean startVisit(SoftwareSystem softwareSystem) {
        return true;
    }

    @Override
    public boolean startVisit(Container container) {
        return false;
    }

    @Override
    public void endVisit(SoftwareSystem softwareSystem, OutputBuilder builder) {
        String table = toAsciidocTable(enhancerInstance.stream());
        File output = builder.outputFor(AgileArchitectureSection.code, softwareSystem, this, "adoc");
        try {
            FileUtils.write(output, table, "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException("Can't believe I can't write the file " + output.getAbsolutePath(), e);
        }
    }

    String toAsciidocTable(Stream<Enhancer> stream) {
        return stream
                .filter(instance -> !instance.getClass().isInstance(this))
                .sorted(Comparator.comparing(Enhancer::priority))
                .map(instance -> String.format("|%d|%s", instance.priority(), getSimpleName(instance)))
                .collect(Collectors.joining("\n\n", "[cols=\"1,1\"]\n" + "|===\n|Priority|Enhancer\n\n", "\n|==="));
    }

    private String getSimpleName(Enhancer instance) {
        String simpleName = instance.getClass().getSimpleName();
        if(simpleName.contains("$")) {
            simpleName = simpleName.substring(0, simpleName.indexOf('$'));
        }
        return simpleName;
    }
}
