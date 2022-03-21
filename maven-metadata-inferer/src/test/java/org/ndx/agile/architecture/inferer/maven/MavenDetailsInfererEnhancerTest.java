package org.ndx.agile.architecture.inferer.maven;

import java.io.File;

import javax.enterprise.inject.Instance;

import org.apache.maven.project.MavenProject;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class MavenDetailsInfererEnhancerTest {

	@Test
	void can_analyze_pom_on_filesystem() {
		MavenDetailsInfererEnhancer enhancer = new MavenDetailsInfererEnhancer();
		MavenProject project = enhancer.readMavenProject(new File("pom.xml").toURI().toString());
		Assertions.assertThat(project).isNotNull();
	}

	/**
	 * As a first test, we use a class that we know we load it from a JAR
	 * (because there are some weird cases when loading from physical PATH)
	 */
	@Test
	void can_analyze_pom_of_provided_class_name_from_a_known_jar() {
		MavenDetailsInfererEnhancer enhancer = new MavenDetailsInfererEnhancer();
		MavenProject project = enhancer.findMavenProjectOf(Instance.class);
		Assertions.assertThat(project).isNotNull();
	}
	@Test
	void can_analyze_pom_of_provided_class_name_from_current_project() {
		MavenDetailsInfererEnhancer enhancer = new MavenDetailsInfererEnhancer();
		MavenProject project = enhancer.findMavenProjectOf(MavenDetailsInfererEnhancer.class);
		Assertions.assertThat(project).isNotNull();
	}

}
