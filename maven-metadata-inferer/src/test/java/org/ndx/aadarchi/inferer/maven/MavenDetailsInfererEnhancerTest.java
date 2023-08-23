package org.ndx.aadarchi.inferer.maven;

import java.util.Arrays;

import javax.inject.Inject;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Test;
import org.ndx.aadarchi.base.ArchitectureEnhancer;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys.ConfigProperties.BasePath;

import com.structurizr.Workspace;
import com.structurizr.model.Container;
import com.structurizr.model.SoftwareSystem;

@EnableWeld
public class MavenDetailsInfererEnhancerTest {
    @WeldSetup
    public WeldInitiator weld = WeldInitiator.performDefaultDiscovery();

    @Inject MavenDetailsInfererEnhancer tested;
	@Inject ArchitectureEnhancer enhancer;
	
	@Inject @ConfigProperty(name=BasePath.NAME, defaultValue = BasePath.VALUE) FileObject basePath;

    @Test public void can_visit_a_software_system_having_an_associated_pom() throws FileSystemException {
    	// Given
    	var w = new Workspace(getClass().getName(), "a test workspace");
    	SoftwareSystem system = w.getModel().addSoftwareSystem("The system to decorate with maven informations");
    	system.addProperty(ModelElementKeys.ConfigProperties.BasePath.NAME, basePath.getName().getPath());
		// When
    	// We emulate in-depth visit (but do not really perform it)
    	enhancer.enhance(w, Arrays.asList(tested));
		// Then
		Assertions.assertThat(system.getProperties())
			.containsOnlyKeys(
					ModelElementKeys.ConfigProperties.BasePath.NAME,
					MavenEnhancer.AGILE_ARCHITECTURE_MAVEN_COORDINATES,
					MavenEnhancer.AGILE_ARCHITECTURE_MAVEN_TECHNOLOGIES,
					ModelElementKeys.Scm.PROJECT,
					ModelElementKeys.ISSUE_MANAGER
					);
		// There are containers in system
		Assertions.assertThat(system.getContainers()).isNotEmpty();
		Container mavenMetadataInferer = system.getContainerWithName("maven-metadata-inferer");
		Container base = system.getContainerWithName("base");
		Assertions.assertThat(mavenMetadataInferer.hasEfferentRelationshipWith(base))
			.isTrue();
		// Those containers have dependencies
		// And the projects all have thei informations filled
		Assertions.assertThat(mavenMetadataInferer)
			.isNotNull()
			.extracting(container -> container.getProperties())
			.asInstanceOf(InstanceOfAssertFactories.MAP)
			.containsOnlyKeys(
					ModelElementKeys.ConfigProperties.BasePath.NAME,
					ModelElementKeys.Scm.PATH,
					MavenEnhancer.AGILE_ARCHITECTURE_MAVEN_COORDINATES,
					MavenEnhancer.AGILE_ARCHITECTURE_MAVEN_POM,
					MavenEnhancer.AGILE_ARCHITECTURE_MAVEN_TECHNOLOGIES,
					ModelElementKeys.Scm.PROJECT,
					ModelElementKeys.JAVA_SOURCES,
					ModelElementKeys.JAVA_PACKAGES,
					ModelElementKeys.ISSUE_MANAGER
					)
			.containsEntry(ModelElementKeys.JAVA_SOURCES,
					basePath.resolveFile("maven-metadata-inferer/src/main/java").getURL().toString()
					)
			.extracting(properties -> properties.get(ModelElementKeys.JAVA_PACKAGES))
			.asInstanceOf(InstanceOfAssertFactories.STRING)
			.contains("org.ndx.aadarchi.inferer.maven")
			;
    }

    @Test public void bug_373_is_fixed() throws FileSystemException {
    	// Given
    	var w = new Workspace(getClass().getName(), "a test workspace");
    	SoftwareSystem system = w.getModel().addSoftwareSystem("The system to decorate with maven informations");
    	system.addProperty(ModelElementKeys.ConfigProperties.BasePath.NAME, basePath.getName().getPath());
		// When
    	enhancer.enhance(w, Arrays.asList(tested));
		// Then
		// There are containers in system
		Assertions.assertThat(system.getContainers()).isNotEmpty();
		// There are added containers
		Container sipocDiagramGenerator = system.getContainerWithName("sipoc-diagram-generator");
		Assertions.assertThat(sipocDiagramGenerator)
			.isNotNull()
			;
		Assertions.assertThat(sipocDiagramGenerator.getTechnology())
			.isNotBlank()
			.isEqualTo("Java");
		Container springComponentDetector = system.getContainerWithName("spring-components-detector");
		Assertions.assertThat(springComponentDetector)
			.isNotNull()
			;
		Assertions.assertThat(springComponentDetector.getTechnology())
			.isNotBlank()
			.isEqualTo("Java");
    }
}
