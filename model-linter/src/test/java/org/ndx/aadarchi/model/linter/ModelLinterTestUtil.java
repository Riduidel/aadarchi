package org.ndx.aadarchi.model.linter;

import com.structurizr.Workspace;

public class ModelLinterTestUtil {
    public static Workspace createWorkspace() {
        Workspace workspace = new Workspace(ModelLinterTestUtil.class.getName(), "a test workspace");
        var system = workspace.getModel().addSoftwareSystem("softwareSystem", "a softwareSystem that verify some useful hings");
        var container = system.addContainer("container", "container description", "Java");
        var component = container.addComponent("component", "component description", "Javascript");
        var container2 = system.addContainer("container2", "container2 description", "");
        var container3 = system.addContainer("container3", "", "");
        var component2 = container.addComponent("component2", "component2 description", "");

        container.uses(container2, "containers relationship with container2", "Java");
        container.uses(container3, "containers relationship with container3", "Javascript");
        container2.uses(container3, "", "");

        return workspace;
    }
}
