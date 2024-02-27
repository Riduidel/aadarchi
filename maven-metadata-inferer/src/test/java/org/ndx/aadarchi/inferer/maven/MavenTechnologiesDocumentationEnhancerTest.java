package org.ndx.aadarchi.inferer.maven;

import java.util.Arrays;
import java.util.Map;

import jakarta.inject.Inject;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Test;
import org.ndx.aadarchi.base.ArchitectureEnhancer;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys.ConfigProperties.BasePath;
import org.ndx.aadarchi.base.utils.commonsvfs.FileObjectDetector;
import org.ndx.aadarchi.cdi.deltaspike.ConfigProperty;
import org.ndx.aadarchi.inferer.maven.technologies.MvnRepositoryArtifactsProducer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.structurizr.Workspace;
import com.structurizr.model.Container;
import com.structurizr.model.SoftwareSystem;

@EnableWeld
public class MavenTechnologiesDocumentationEnhancerTest {
    @WeldSetup
    public WeldInitiator weld = WeldInitiator.performDefaultDiscovery();

    @Inject MavenTechnologiesDocumentationEnhancer tested;
    @Inject MavenDetailsInfererEnhancer mavenReader;
	@Inject ArchitectureEnhancer enhancer;
	
	@Inject @ConfigProperty(name=BasePath.NAME, defaultValue = BasePath.VALUE) FileObject basePath;

    @Test public void can_detect_dependencies_versions_declared_through_dependency_management_and_properties() throws FileSystemException, JsonMappingException, JsonProcessingException {
    	// Given
    	var w = new Workspace(getClass().getName(), "a test workspace");
    	SoftwareSystem system = w.getModel().addSoftwareSystem("The system to decorate with maven informations");
    	system.addProperty(ModelElementKeys.ConfigProperties.BasePath.NAME, basePath.getName().getPath());
		// When
    	// We emulate in-depth visit (but do not really perform it)
    	enhancer.enhance(w, mavenReader, tested);
		// Then
		// There are containers in system
		Assertions.assertThat(system.getContainers()).isNotEmpty();
		Container mavenMetadataInferer = system.getContainerWithName("maven-metadata-inferer");
		// For this container, we will try to see what versions are given for some dependencies
		Assertions.assertThat(mavenMetadataInferer.getProperties())
			.containsKey(MavenEnhancer.AGILE_ARCHITECTURE_MAVEN_TECHNOLOGIES);
		// Now extract the dependencies
		ObjectMapper objectMapper = new ObjectMapper();
		Map<String, String> dependenciesVersions = objectMapper.readValue(
				mavenMetadataInferer.getProperties().get(MavenEnhancer.AGILE_ARCHITECTURE_MAVEN_TECHNOLOGIES)
				, MavenEnhancer.AGILE_ARCHITECTURE_MAVEN_TECHNOLOGIES_TYPE);
		// Yeah, i'm searching for the dependency that make that very line of code possible
		// So it should not fail
		Assertions.assertThat(dependenciesVersions)
			.extractingByKey("org.assertj.assertj-core")
			.isEqualTo("3.23.1");
		Assertions.assertThat(mavenMetadataInferer.getTechnology()).doesNotStartWith(",");
    }
}
