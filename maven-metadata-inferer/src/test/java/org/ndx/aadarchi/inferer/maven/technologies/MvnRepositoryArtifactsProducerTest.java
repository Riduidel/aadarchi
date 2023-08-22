package org.ndx.aadarchi.inferer.maven.technologies;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.assertj.core.api.Assertions;
import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Test;

@EnableWeld
class MvnRepositoryArtifactsProducerTest {

    @WeldSetup
    public WeldInitiator weld = WeldInitiator.performDefaultDiscovery();
    
    @Inject @Named(MvnRepositoryArtifactsProducer.MVNREPOSITORY_ARTIFACTS) Map<String, MvnRepositoryArtifact> data;

	@Test
	void mvnrepository_has_some_artifacts_in() {
		Assertions.assertThat(data).isNotNull();
		Assertions.assertThat(data).isNotEmpty();
		Assertions.assertThat(data)
			.containsKey("junit.junit")
			.extractingByKey("junit.junit")
			.hasFieldOrPropertyWithValue("name", "JUnit")
			.hasFieldOrPropertyWithValue("coordinates", "junit.junit")
			;
	}

}
