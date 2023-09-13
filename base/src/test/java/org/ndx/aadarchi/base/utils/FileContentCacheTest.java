package org.ndx.aadarchi.base.utils;

import java.io.IOException;

import javax.inject.Inject;

import org.apache.commons.vfs2.FileObject;
import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.assertj.core.api.Assertions;
import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Test;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys.ConfigProperties.BasePath;

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
		// When
		try(var input = tested.openStreamFor(readme)) {
			Assertions.assertThat(input).isNotEmpty();
		}
	}
}
