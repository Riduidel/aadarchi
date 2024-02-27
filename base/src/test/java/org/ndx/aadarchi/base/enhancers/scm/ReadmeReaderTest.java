package org.ndx.aadarchi.base.enhancers.scm;

import jakarta.inject.Inject;

import org.assertj.core.util.Arrays;
import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Test;
import org.ndx.aadarchi.base.ArchitectureEnhancer;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys;

import com.structurizr.Workspace;

@EnableWeld
class ReadmeReaderTest {

    @WeldSetup
    public WeldInitiator weld = WeldInitiator.performDefaultDiscovery();

    @Inject ReadmeReader tested;
    @Inject ArchitectureEnhancer enhancer;

	@Test
	void bug_81_can_read_a_local_readme_file() {
    	// Given
    	var w = new Workspace(getClass().getName(), "a test workspace");
    	var system = w.getModel().addSoftwareSystem("The system to add a readme to");
    	system.addProperty(ModelElementKeys.ConfigProperties.BasePath.NAME, ".");
		// When
    	enhancer.enhance(w, tested);
	}

}
