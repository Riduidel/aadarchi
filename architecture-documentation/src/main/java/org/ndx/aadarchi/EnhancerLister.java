package org.ndx.aadarchi;

import com.structurizr.model.Container;
import com.structurizr.model.SoftwareSystem;
import org.apache.commons.io.FileUtils;
import org.ndx.aadarchi.base.AgileArchitectureSection;
import org.ndx.aadarchi.base.Enhancer;
import org.ndx.aadarchi.base.OutputBuilder;
import org.ndx.aadarchi.base.enhancers.ModelElementAdapter;

import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Enhancer that list all the enhancers
 * The enhancers list contain the priority and the name of the loaded enhancers
 * The list is generated in an Adoc file in a table format
 * @author jason-sycz feat nicolas-delsaux
 *
 */

public class EnhancerLister extends ModelElementAdapter {
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
        builder.writeToOutput(AgileArchitectureSection.code, softwareSystem, this, OutputBuilder.Format.adoc, table);
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
