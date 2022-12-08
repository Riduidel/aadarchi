package org.ndx.aadarchi.inferer.maven;

import java.io.File;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.apache.maven.project.MavenProject;
import org.assertj.core.api.Assertions;
import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@EnableWeld
class MavenDetailsInfererEnhancerTest {

    @WeldSetup
    public WeldInitiator weld = WeldInitiator.performDefaultDiscovery();

    private MavenPomReader mavenPomReader;
    
    @BeforeEach public void loadMavenPomReader() {
    	mavenPomReader = weld.select(MavenPomReader.class).get();
    }

	@Test
	void can_analyze_pom_on_filesystem() {
		MavenProject project = mavenPomReader.readMavenProject(new File("pom.xml").toURI().toString());
		Assertions.assertThat(project).isNotNull();
	}

	/**
	 * As a first test, we use a class that we know we load it from a JAR
	 * (because there are some weird cases when loading from physical PATH)
	 */
	@Test
	void can_analyze_pom_of_provided_class_name_from_a_known_jar() {
		MavenProject project = mavenPomReader.findMavenProjectOf(Instance.class);
		Assertions.assertThat(project).isNotNull();
	}
	@Test
	void can_analyze_pom_of_provided_class_name_from_current_project() {
		MavenProject project = mavenPomReader.findMavenProjectOf(MavenDetailsInfererEnhancer.class);
		Assertions.assertThat(project).isNotNull();
	}

}
