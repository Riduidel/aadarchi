package org.ndx.aadarchi.base.enhancers.scm;

import java.io.File;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.provider.local.LocalFileSystem;
import org.assertj.core.api.Assertions;
import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Test;
import org.ndx.aadarchi.base.AgileArchitectureSection;
import org.ndx.aadarchi.base.ArchitectureEnhancer;
import org.ndx.aadarchi.base.OutputBuilder.Format;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys;

import com.structurizr.Workspace;

import jakarta.inject.Inject;

@EnableWeld
class ReadmeReaderTest {

    @WeldSetup
    public WeldInitiator weld = WeldInitiator.performDefaultDiscovery();

    @Inject ReadmeReader tested;
    @Inject ArchitectureEnhancer enhancer;

	@Test
	void bug_81_can_read_a_local_readme_file() {
    	// Given
		String METHOD_NAME = "bug_81_can_read_a_local_readme_file"; 
    	var w = new Workspace(getClass().getName(), "a test workspace");
    	var system = w.getModel().addSoftwareSystem(getClass().getName()+"#"+METHOD_NAME);
    	system.addProperty(ModelElementKeys.ConfigProperties.BasePath.NAME, ".");
		// When
    	enhancer.enhance(w, tested);
    	// Then
    	FileObject readme = enhancer.getOutputBuilder().outputFor(AgileArchitectureSection.code, system, tested, Format.adoc);
    	Assertions.assertThat(readme.getFileSystem()).isInstanceOf(LocalFileSystem.class);
    	// yeah obviously we generated a local file, so let's convert it to a classical file
    	File readmeFile = readme.getPath().toFile();
    	Assertions.assertThat(readmeFile).exists()
    		.isNotEmpty()
    		.content().contains("aadarchi");
		}
}
