package org.ndx.aadarchi.sipoc.diagram.generator;

import java.io.File;

import javax.inject.Inject;

import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.assertj.core.api.Assertions;
import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ndx.aadarchi.base.AgileArchitectureSection;
import org.ndx.aadarchi.base.OutputBuilder;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys.ConfigProperties.EnhancementsDir;
import org.ndx.aadarchi.base.utils.SimpleOutputBuilder;

import com.structurizr.Workspace;
import com.structurizr.model.SoftwareSystem;

@EnableWeld
class SipocEnhancerTest {
    @WeldSetup
    public WeldInitiator weld = WeldInitiator.performDefaultDiscovery();

    @Inject SipocEnhancer tested;

	@Inject OutputBuilder outputBuilder;

	@Test
	void test() {
    	// Given
    	var w = new Workspace(getClass().getName(), "a test workspace");
    	SoftwareSystem system = w.getModel().addSoftwareSystem("The system to decorate with maven informations");
		// When
    	// We emulate in-depth visit (but do not really perform it)
		tested.startVisit(w, null);
		tested.startVisit(system);
		tested.endVisit(system, null);
		tested.endVisit(w, null);
		// Then
		File outputFolderForSystem = outputBuilder.outputFor(AgileArchitectureSection.code, system, tested, OutputBuilder.Format.adoc);
		Assertions.assertThat(outputFolderForSystem)
			.isDirectoryContaining("glob:*.adoc");
	}

}
