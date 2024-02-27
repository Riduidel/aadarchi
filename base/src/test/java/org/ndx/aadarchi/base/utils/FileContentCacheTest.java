package org.ndx.aadarchi.base.utils;

import java.io.IOException;

import jakarta.inject.Inject;

import org.apache.commons.vfs2.FileObject;
import org.assertj.core.api.Assertions;
import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.Preconditions;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys.ConfigProperties.BasePath;
import org.ndx.aadarchi.cdi.deltaspike.ConfigProperty;

@EnableWeld
class FileContentCacheTest {

    @WeldSetup
    public WeldInitiator weld = WeldInitiator.performDefaultDiscovery();

    @Inject FileContentCache tested;
	@Inject @ConfigProperty(name=BasePath.NAME, defaultValue = BasePath.VALUE) FileObject basePath;

	@Test
	void bug_81_can_read_a_local_readme_file() throws IOException {
    	// Given
		var readme = basePath.resolveFile("README.md");
		Preconditions.condition(readme.exists(), "Readme file should exist");
		Preconditions.condition(readme.getContent().getSize()>0, "There should be some content in readme");
		// When
		try(var input = tested.openStreamFor(readme)) {
			Assertions.assertThat(input).isNotNull();
		}
	}
}
