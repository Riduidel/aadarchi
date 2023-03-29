package org.ndx.aadarchi.model.linter;

import com.structurizr.Workspace;
import com.structurizr.model.Component;
import com.structurizr.model.Container;
import com.structurizr.model.Model;
import com.structurizr.model.SoftwareSystem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.LoggerFactory;

import static org.slf4j.LoggerFactory.getILoggerFactory;

public abstract class AbstractModelLinterTest {

    private static MemoryAppender memoryAppender;
    private static final String LOGGER_NAME = "org.ndx.aadarchi.model.linter";
    private static final String MSG = "This is a test message!!!";

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

        Logger logger = (Logger) LoggerFactory.getLogger(LOGGER_NAME);
        memoryAppender = new MemoryAppender();
        memoryAppender.setContext((LoggerContext) getILoggerFactory());
        logger.setLevel(Level.ERROR);
        logger.addAppender(memoryAppender);
        memoryAppender.start();
    }

    @AfterEach
    public void cleanUp() {
        memoryAppender.reset();
        memoryAppender.stop();
    }
}