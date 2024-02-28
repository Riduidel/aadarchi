package org.ndx.aadarchi.github;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;

import jakarta.inject.Inject;

import org.assertj.core.api.Assertions;
import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Test;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

@EnableWeld
class GitHubProviderTest {
	@WeldSetup
	public WeldInitiator weld = WeldInitiator.performDefaultDiscovery();
	
	@Inject GitHub github;

	@Test
	void can_load_github_client_in_test() throws IOException {
		Assertions.assertThat(github).isNotNull();
	}
	
	@Test void can_read_some_repository_details() throws IOException {
		GHRepository repository = github.getRepository("Riduidel/aadarchi");
		Assertions.assertThat(repository).isNotNull();
		Assertions.assertThat(repository.getDescription()).isNotNull();
	}

}
