package org.ndx.aadarchi.inferer.maven;

import static org.junit.jupiter.api.Assertions.fail;

import javax.inject.Inject;
import javax.inject.Named;

import org.assertj.core.api.Assertions;
import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

@EnableWeld
class MvnRepositoryArtifactsProducerTest {

    @WeldSetup
    public WeldInitiator weld = WeldInitiator.performDefaultDiscovery();
    
    @Inject JSONObject data;
/*
	@Test
	void mvnrepository_has_some_artifacts_in() {
		Assertions.assertThat(data).isNotNull();
		Assertions.assertThat(data.toMap()).isNotEmpty();
		
	}
*/
}
