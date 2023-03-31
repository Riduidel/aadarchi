package org.ndx.aadarchi.gitlab.vfs;

import javax.inject.Inject;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.assertj.core.api.Assertions;
import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.ndx.aadarchi.gitlab.Constants;
import org.ndx.aadarchi.gitlab.GitlabSCMHandler;

@EnableWeld
class GitLabFileSystemProviderTest {
	@WeldSetup
	public WeldInitiator weld = WeldInitiator.performDefaultDiscovery();
	
	@Inject GitLabFileSystemProvider gitHubFileSystem;
	
	@BeforeAll public static void setGitlabServer() {
		System.setProperty(Constants.CONFIG_GITLAB_URL, "framapiaf.org");
	}

	@Test
	void test() throws FileSystemException {
		// Given
		FileObject repositoryRoot = gitHubFileSystem.getProjectRoot("Riduidel/codingame-maven-plugins");
		// When
		FileObject readme = repositoryRoot.getChild("README.md");
		// Then
		Assertions.assertThat((Object) readme).isNotNull();
		Assertions.assertThat(readme.getContent().getSize()).isGreaterThan(100);
	}

}
