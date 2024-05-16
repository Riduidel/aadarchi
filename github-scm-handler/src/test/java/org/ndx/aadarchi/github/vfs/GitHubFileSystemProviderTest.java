package org.ndx.aadarchi.github.vfs;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.filter.RegexFileFilter;
import org.assertj.core.api.Assertions;
import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Test;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys;
import org.ndx.aadarchi.base.utils.commonsvfs.FileObjectDetector;

import com.structurizr.Workspace;
import com.structurizr.model.SoftwareSystem;

import jakarta.inject.Inject;

@EnableWeld
class GitHubFileSystemProviderTest {
	@WeldSetup
	public WeldInitiator weld = WeldInitiator.performDefaultDiscovery();
	
	@Inject GitHubFileSystemOptionsConfigurer gitHubFileSystem;
	@Inject FileSystemManager fileSystemManager;
	@Inject GitHubRootProvider gitHubRootProvider;
	@Inject FileObjectDetector detector;

	@Test
	void test() throws FileSystemException {
		// Given
		FileObject repositoryRoot = gitHubRootProvider.getProjectRoot("Riduidel/aadarchi");
		// When
		FileObject readme = repositoryRoot.getChild("README.md");
		// Then
		Assertions.assertThat((Object) readme).isNotNull();
		Assertions.assertThat(readme.getContent().getSize()).isGreaterThan(100);
	}


	@Test
	void bug_432_is_resolved() throws FileSystemException {
		// Given
    	var w = new Workspace(getClass().getName(), "a test workspace");
    	SoftwareSystem system = w.getModel().addSoftwareSystem("The system which has an associated file to read");
    	system.addProperty(ModelElementKeys.ConfigProperties.BasePath.NAME, "github://github.com/Riduidel/aadarchi");
		// When
		detector.whenFileDetected(system, 
				new RegexFileFilter("(readme|README)\\.(adoc|md)"), 
		// Then
				elementRoot -> { Assertions.fail("We should detect one readme file"); }, 
				(elementRoot, readme) -> Assertions.assertThat((Object) readme).isNotNull(), 
				(elementRoot, files) -> Assertions.fail("We should detect one readme file"));
	}

}
