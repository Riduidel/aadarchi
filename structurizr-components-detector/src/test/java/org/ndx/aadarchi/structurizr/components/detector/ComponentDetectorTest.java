package org.ndx.aadarchi.structurizr.components.detector;

import berlin.yuna.wiserjunit.model.annotation.WiserJunitReport;
import com.structurizr.Workspace;
import com.structurizr.analysis.ComponentFinder;
import org.assertj.core.api.Assertions;
import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.structurizr.model.Component;
import com.structurizr.model.Container;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.inject.Inject;
import java.util.Set;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@EnableWeld
@WiserJunitReport
class ComponentDetectorTest {
    @WeldSetup
    public WeldInitiator weld = WeldInitiator.performDefaultDiscovery();
    @Mock ComponentFinder componentFinder;
    @Inject ComponentDetector componentDetector;

    @BeforeEach public void addLogHandler() {
        logger = Logger.getLogger(ComponentDetector.class.getName());


        // TODO add memorizing log handler
    }

    @AfterEach public void removeLogHandler() {

    }

    @Test
    void write_the_correct_logs_when_detecting_components() throws Throwable {
        //Given
        Workspace w = new Workspace(getClass().getSimpleName(), "a workspace for testing component addition");
        var system = w.getModel().addSoftwareSystem("system");
        var container = system.addContainer("container");
        Mockito.when(componentFinder.findComponents())
                .thenReturn(Set.of(container.addComponent("a"), container.addComponent("b")));
        //When
        componentDetector.doDetectComponentsIn(container, componentFinder);
        //Then
        //Assertions.assertThat(logger.).isEqualTo();
    }
}