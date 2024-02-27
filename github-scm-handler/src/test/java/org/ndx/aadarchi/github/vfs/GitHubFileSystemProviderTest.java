package org.ndx.aadarchi.github.vfs;

import jakarta.inject.Inject;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.assertj.core.api.Assertions;
import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Test;

@EnableWeld
class GitHubFileSystemProviderTest {
	@WeldSetup
	public WeldInitiator weld = WeldInitiator.performDefaultDiscovery();
	
	@Inject GitHubFileSystemProvider gitHubFileSystem;

	@Test
	void test() throws FileSystemException {
		// Given
		FileObject repositoryRoot = gitHubFileSystem.getProjectRoot("Riduidel/aadarchi");
		// When
		FileObject readme = repositoryRoot.getChild("README.md");
		// Then
		Assertions.assertThat((Object) readme).isNotNull();
		Assertions.assertThat(readme.getContent().getSize()).isGreaterThan(100);
	}

}
