package org.ndx.aadarchi.model.linter;

import com.structurizr.Workspace;

public class ModelLinterTestUtil {
    public static Workspace createWorkspace() {
        Workspace workspace = new Workspace(ModelLinterTestUtil.class.getName(), "a test workspace");
        var system = workspace.getModel().addSoftwareSystem("softwareSystem", "a softwareSystem that verify some useful hings");
        var container = system.addContainer("container", "container", "Java");
        var component = container.addComponent("component", "component", "Java");
        var container2 = system.addContainer("container2", "", "");
        var container3 = system.addContainer("container3", "", "");
        var component2 = container.addComponent("component2", "", "");

        container.uses(container2, "containers relathionship", "Java");
        container2.uses(container3, "", "");

        return workspace;
    }
}
