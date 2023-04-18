package org.ndx.aadarchi.model.linter;

import com.structurizr.Workspace;
import com.structurizr.model.Component;
import com.structurizr.model.Container;
import com.structurizr.model.Model;
import com.structurizr.model.SoftwareSystem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractModelLinterTest {
    LoggerHandler loggerHandler;

    protected SoftwareSystem system;
    protected Workspace workspace;
    Container container;
    Container container2;
    Container container3;
    Component component;
    Component component2;

    @BeforeEach
    void createModel() {
        workspace = ModelLinterTestUtil.createWorkspace();
        Model model = workspace.getModel();
        system = model.getSoftwareSystems().iterator().next();
        container = system.getContainerWithName("container");
        assert container != null;
        component = container.getComponentWithName("component");
        container2 = system.getContainerWithName("container2");
        component2 = container.getComponentWithName("component2");
        container3 = system.getContainerWithName("container3");

        loggerHandler = new LoggerHandler();
        Logger logger = Logger.getLogger(ModelLinter.class.getName());
        loggerHandler.setLevel(Level.ALL);
        logger.setUseParentHandlers(false);
        logger.addHandler(loggerHandler);
        logger.setLevel(Level.ALL);
    }

    @AfterEach
    public void cleanUp() {
        loggerHandler.close();
    }
}