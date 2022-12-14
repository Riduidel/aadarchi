package org.ndx.aadarchi.inferer.maven;

import java.io.File;
import java.util.Arrays;

import javax.inject.Inject;

import org.apache.maven.project.MavenProject;
import org.assertj.core.api.Assertions;
import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Test;
import org.ndx.aadarchi.base.ArchitectureEnhancer;
import org.ndx.aadarchi.base.OutputBuilder;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys;

import com.structurizr.Workspace;
import com.structurizr.annotation.UsesComponent;
import com.structurizr.model.SoftwareSystem;

@EnableWeld
public class MavenDetailsInfererEnhancerTest {
    @WeldSetup
    public WeldInitiator weld = WeldInitiator.performDefaultDiscovery();

    @Inject MavenDetailsInfererEnhancer tested;
	@Inject ArchitectureEnhancer enhancer;

    @Test public void can_visit_a_software_system_having_an_associated_pom() {
    	// Given
    	var w = new Workspace(getClass().getName(), "a test workspace");
    	SoftwareSystem system = w.getModel().addSoftwareSystem("The system to decorate with maven informations");
    	system.addProperty(ModelElementKeys.ConfigProperties.BasePath.NAME, new File(".").getAbsolutePath());
		// When
    	// We emulate in-depth visit (but do not really perform it)
    	enhancer.enhance(w, Arrays.asList(tested));
		// Then
		Assertions.assertThat(system.getProperties())
			.containsOnlyKeys(
					ModelElementKeys.ConfigProperties.BasePath.NAME,
					MavenEnhancer.AGILE_ARCHITECTURE_MAVEN_COORDINATES,
					ModelElementKeys.Scm.PROJECT,
					ModelElementKeys.JAVA_SOURCES,
					ModelElementKeys.JAVA_PACKAGES,
					ModelElementKeys.ISSUE_MANAGER
					);
    }
}
