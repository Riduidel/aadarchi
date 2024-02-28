package org.ndx.aadarchi.base.utils;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.AllFileSelector;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSelectInfo;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.provider.local.LocalFile;
import org.assertj.core.api.Assertions;
import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.Preconditions;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys.ConfigProperties.BasePath;
import org.ndx.aadarchi.cdi.deltaspike.ConfigProperty;

import com.pivovarit.function.ThrowingPredicate;

import jakarta.inject.Inject;

@EnableWeld
class FileContentCacheTest {

    @WeldSetup
    public WeldInitiator weld = WeldInitiator.performDefaultDiscovery();

    @Inject FileContentCache tested;
	@Inject @ConfigProperty(name=BasePath.NAME, defaultValue = BasePath.VALUE) FileObject basePath;
	@Inject @ConfigProperty(
			name = ModelElementKeys.ConfigProperties.CacheDir.NAME, 
			defaultValue = ModelElementKeys.ConfigProperties.CacheDir.VALUE) FileObject cacheDir;
	@Inject FileSystemManager fileSystemManager;

	@Test
	void bug_81_can_read_a_local_readme_file() throws IOException {
    	// Given
		var readme = basePath.resolveFile("README.md");
		Preconditions.condition(readme.exists(), "Readme file should exist");
		Preconditions.condition(readme.getContent().getSize()>0, "There should be some content in readme");
		// When
		String readmeText = IOUtils.toString(tested.openStreamFor(readme), "UTF-8");
		// Then
		Assertions.assertThat(readmeText).containsIgnoringCase("aadarchi");
	}

	@Test
	void bug_81_can_have_a_remote_unknown_file_inserted_into_cache() throws IOException {
    	// Given
		var readme = fileSystemManager.resolveFile("http5s://github.com/Riduidel/aadarchi/raw/main/README.md");
		Preconditions.condition(!(readme instanceof LocalFile), "Since readme come from the web, it should be remote, no?");
		// We clear the cache before any operation
		tested.cacheDir = cacheDir;
		tested.cacheDir.deleteAll();
		// When
		try(var input = tested.openStreamFor(readme)) {
			// There is in fact nothing else to do : the file will be automatically downloaded
		}
		// Then
		FileObject[] localReadme = tested.cacheDir.findFiles(new AllFileSelector() {
			@Override
			public boolean includeFile(FileSelectInfo fileInfo) throws Exception {
				return "readme.md".equals(fileInfo.getFile().getName().getBaseName().toLowerCase());
			}
		});
		Assertions.assertThat(localReadme)
			.isNotEmpty()
			.allMatch(ThrowingPredicate.unchecked(file -> !file.getContent().isEmpty()));
	}
}
