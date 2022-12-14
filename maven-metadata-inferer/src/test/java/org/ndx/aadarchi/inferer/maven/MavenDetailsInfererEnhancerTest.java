package org.ndx.aadarchi.inferer.maven;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.inject.Inject;

import org.apache.maven.project.MavenProject;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.assertj.core.api.InstanceOfAssertFactory;
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
    	system.addProperty(ModelElementKeys.ConfigProperties.BasePath.NAME, getAadarchiRootPath());
		// When
    	// We emulate in-depth visit (but do not really perform it)
    	enhancer.enhance(w, Arrays.asList(tested));
		// Then
		Assertions.assertThat(system.getProperties())
			.containsOnlyKeys(
					ModelElementKeys.ConfigProperties.BasePath.NAME,
					MavenEnhancer.AGILE_ARCHITECTURE_MAVEN_COORDINATES,
					ModelElementKeys.Scm.PROJECT,
					ModelElementKeys.ISSUE_MANAGER
					);
		// There are containers in system
		Assertions.assertThat(system.getContainers()).isNotEmpty();
		// There is even a contain
		Assertions.assertThat(system.getContainerWithName("maven-metadata-inferer"))
			.isNotNull()
			.extracting(container -> container.getProperties())
			.asInstanceOf(InstanceOfAssertFactories.MAP)
			.containsOnlyKeys(
					MavenEnhancer.AGILE_ARCHITECTURE_MAVEN_POM,
					ModelElementKeys.Scm.PATH,
					MavenEnhancer.AGILE_ARCHITECTURE_MAVEN_COORDINATES,
					ModelElementKeys.Scm.PROJECT,
					ModelElementKeys.JAVA_SOURCES,
					ModelElementKeys.JAVA_PACKAGES,
					ModelElementKeys.ISSUE_MANAGER
					);
			
    }

    /**
     * Obtain the aadarchi root path
     * In fact, this method simply checks if the given path is the maven-metadata-inferer one.
     * If so, it returns the parent, otherwise, it returns itself
     * (this allows us to run the test both from JUnit and in Reactor build)
     */
	public static String getAadarchiRootPath() {
		File current = new File(".");
		try {
			current = current.getCanonicalFile();
			if(current.getName().equals("maven-metadata-inferer"))
				return current.getParent();
			else
				return current.getAbsolutePath();
		} catch (IOException e) {
			throw new RuntimeException("We should be able to get this folder path, no?", e);
		}
	}
}
