package org.ndx.aadarchi.inferer.maven.technologies;

import java.util.Map;

import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.assertj.core.api.Assertions;
import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Test;
import org.ndx.aadarchi.base.utils.FileContentCache;

@EnableWeld
class MvnRepositoryArtifactsProducerTest {

    @WeldSetup
    public WeldInitiator weld = WeldInitiator.performDefaultDiscovery();
    
    @Inject @Named(MvnRepositoryArtifactsProducer.MVNREPOSITORY_ARTIFACTS) Map<String, MvnRepositoryArtifact> data;
    
    @Inject FileContentCache cache;

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
