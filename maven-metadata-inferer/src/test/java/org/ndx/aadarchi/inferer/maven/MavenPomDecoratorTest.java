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
import org.ndx.aadarchi.base.enhancers.ModelElementKeys;

import com.structurizr.Workspace;

@EnableWeld
public class MavenPomDecoratorTest {
    @WeldSetup
    public WeldInitiator weld = WeldInitiator.performDefaultDiscovery();

    @Inject
    private MavenPomDecorator decorator;
    @Inject private MavenPomReader reader;
    
    @Test
    public void can_decorate_software_system() {
    	// Given
    	var w = new Workspace(getClass().getName(), "a test workspace");
    	var system = w.getModel().addSoftwareSystem("The system to decorate with maven informations");
		MavenProject project = reader.readMavenProject(new File("../pom.xml").toURI().toString());
		Assertions.assertThat(project).isNotNull();
		// When
		decorator.decorate(system, project);
		// Then
		Assertions.assertThat(system.getProperties())
		.containsOnlyKeys(
				MavenEnhancer.AGILE_ARCHITECTURE_MAVEN_COORDINATES,
				MavenEnhancer.AGILE_ARCHITECTURE_MAVEN_TECHNOLOGIES,
				ModelElementKeys.Scm.PROJECT,
				ModelElementKeys.ISSUE_MANAGER
				);
    }
    
    @Test
    public void can_decorate_container() {
    	// Given
    	var w = new Workspace(getClass().getName(), "a test workspace");
    	var system = w.getModel().addSoftwareSystem("The system to decorate with maven informations");
    	var container = system.addContainer("maven-metadata-inferer");
		MavenProject project = reader.readMavenProject(new File("pom.xml").toURI().toString());
		Assertions.assertThat(project).isNotNull();
		// When
		decorator.decorate(container, project);
		// Then
		Assertions.assertThat(container.getDescription()).isNotNull();
		Assertions.assertThat(container.getTechnology())
			.isNotNull()
			.containsIgnoringCase("Java")
			.containsIgnoringCase("Maven");
		Assertions.assertThat(container.getProperties())
			.containsOnlyKeys(
							MavenEnhancer.AGILE_ARCHITECTURE_MAVEN_COORDINATES,
							MavenEnhancer.AGILE_ARCHITECTURE_MAVEN_TECHNOLOGIES,
							ModelElementKeys.Scm.PROJECT,
							ModelElementKeys.JAVA_SOURCES,
							ModelElementKeys.JAVA_PACKAGES,
							ModelElementKeys.ISSUE_MANAGER);
    }
}
