package org.ndx.aadarchi.inferer.maven;

import java.io.File;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.apache.maven.project.MavenProject;
import org.assertj.core.api.Assertions;
import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys.ConfigProperties.BasePath;

import com.structurizr.Workspace;

@EnableWeld
class MavenPomReaderTest {

    @WeldSetup
    public WeldInitiator weld = WeldInitiator.performDefaultDiscovery();

    @Inject FileSystemManager fsManager;
    @Inject MavenPomReader mavenPomReader;
	@Inject @ConfigProperty(name=BasePath.NAME, defaultValue = BasePath.VALUE) FileObject basePath;
    
    @Test public void can_process_pom_declared_through_class() {
    	// Given
    	var w = new Workspace(getClass().getName(), "a test workspace");
    	var system = w.getModel().addSoftwareSystem("The system to decorate with maven informations");
    	system.addProperty(MavenEnhancer.AGILE_ARCHITECTURE_MAVEN_CLASS, MavenPomReader.class.getName());
		// When
    	var project = mavenPomReader.processModelElement(system);
		// Then
    	Assertions.assertThat(project)
    		.get()
    		.extracting(mavenProject -> mavenProject.getArtifactId()).isEqualTo("maven-metadata-inferer")
    		;
    }
    
    @Test public void can_process_pom_declared_through_base_path() {
    	// Given
    	var w = new Workspace(getClass().getName(), "a test workspace");
    	var system = w.getModel().addSoftwareSystem("The system to decorate with maven informations");
    	system.addProperty(ModelElementKeys.ConfigProperties.BasePath.NAME, basePath.getName().getPath());
		// When
    	var project = mavenPomReader.processModelElement(system);
		// Then
    	Assertions.assertThat(project)
    		.get()
    		.extracting(mavenProject -> mavenProject.getArtifactId()).isEqualTo("system")
    		;
    }
    
	@Test
	void can_read_pom_on_filesystem() {
		MavenProject project = mavenPomReader.readMavenProject(new File("pom.xml").toURI().toString());
		Assertions.assertThat(project).isNotNull();
	}
    
	@Test
	void cannot_read_pom_of_missing_file() {
		Assertions.assertThatThrownBy(() -> mavenPomReader.readMavenProject(""))
			.isInstanceOf(MavenDetailsInfererException.class);
	}

	/**
	 * As a first test, we use a class that we know we load it from a JAR
	 * (because there are some weird cases when loading from physical PATH)
	 */
	@Test
	void can_find_pom_of_provided_class_name_from_a_known_jar() {
		FileObject project = mavenPomReader.findMavenPomFrom(Instance.class);
		Assertions.assertThat((Object) project).isNotNull();
	}
	@Test
	void can_find_pom_of_provided_class_name_from_current_project() {
		FileObject project = mavenPomReader.findMavenPomFrom(MavenDetailsInfererEnhancer.class);
		Assertions.assertThat((Object) project).isNotNull();
	}

}
