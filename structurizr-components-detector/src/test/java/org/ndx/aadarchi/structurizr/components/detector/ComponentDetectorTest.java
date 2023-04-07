package org.ndx.aadarchi.structurizr.components.detector;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.structurizr.Workspace;
import com.structurizr.analysis.ComponentFinder;

@ExtendWith(MockitoExtension.class)
@EnableWeld
class ComponentDetectorTest {
	private static class MemorizingHandler extends Handler {
		private List<LogRecord> recordList = new ArrayList<>();

		@Override
		public void publish(LogRecord record) {
			recordList.add(record);
		}

		@Override
		public void flush() {}

		@Override
		public void close() throws SecurityException {}
		
	}
    @WeldSetup
    public WeldInitiator weld = WeldInitiator.performDefaultDiscovery();
    @Mock ComponentFinder componentFinder;
    @Inject ComponentDetector componentDetector;
	private MemorizingHandler logMemory;

    @BeforeEach public void addLogHandler() {
        Logger logger = Logger.getLogger(ComponentDetector.class.getName());
        logger.addHandler(logMemory = new MemorizingHandler());
    }

    @AfterEach public void removeLogHandler() {
        Logger logger = Logger.getLogger(ComponentDetector.class.getName());
        logger.removeHandler(logMemory);
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
        Assertions.assertThat(logMemory.recordList)
        	.hasSize(1)
        	.element(0)
        	.extracting(LogRecord::getMessage)
        	.isEqualTo("Detected 2 new components in container.");
    }
}