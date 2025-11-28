package org.ndx.aadarchi.base.utils.commonsvfs;

import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.filter.NameFileFilter;
import org.apache.commons.vfs2.filter.RegexFileFilter;
import org.assertj.core.api.Assertions;
import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys;
import org.ndx.aadarchi.base.utils.StructurizrUtils;

import com.pivovarit.function.ThrowingBiConsumer;
import com.structurizr.Workspace;
import com.structurizr.model.SoftwareSystem;

import jakarta.inject.Inject;

/**
 * Test is moved in github scm handler due to weird dependencies
 */
@EnableWeld
public class FileObjectDetectorTest {
    private static SoftwareSystem AADARCHI;

	private static Workspace WORKSPACE;

	@WeldSetup
    public WeldInitiator weld = WeldInitiator.performDefaultDiscovery();

    @Inject FileObjectDetector tested;
	@Inject FileSystemManager fileSystemManager;
	
	@BeforeAll public static void createModelElements() {
		WORKSPACE = new Workspace(FileObjectDetectorTest.class.getSimpleName(), "Tests for FileObjectDetectorTest");
		AADARCHI = WORKSPACE.getModel().addSoftwareSystem("aadarchi");
		AADARCHI.addProperty(ModelElementKeys.Scm.PROJECT, "Riduidel/aadarchi.git");
	}

	@Test public void can_read_readme_from_github() {
		// Given
		// When
		tested.whenFileDetected(
			AADARCHI, 
			new RegexFileFilter("(readme|README)\\.(adoc|md)"), 
			// No file detected
			elementRoot -> { Assertions.fail(String.format(
					"Couldn't find any file matching %s for element %s ",
					StructurizrUtils.getCanonicalPath(AADARCHI), elementRoot)); },
			// One file detected
			ThrowingBiConsumer.unchecked((elementRoot, readme) -> { 
				Assertions.assertThat(readme.exists()).isTrue();
			}),
			// on multiple file detected
			(elementRoot, detectedFiles) -> { Assertions.fail(String.format(
					"There are more than one valid file matching %s for element %s"
							+ "(path is %s)",
					StructurizrUtils.getCanonicalPath(AADARCHI), elementRoot)); }
			);
	}

	@Test public void can_fail_correctly_when_file_not_found() {
		// Given
		// When
		tested.whenFileDetected(
			AADARCHI, 
			new NameFileFilter("this_file_doesn_not_exists"), 
			// No file detected
			elementRoot -> {  },
			// One file detected
			(elementRoot, detectedFiles) -> { Assertions.fail(String.format(
					"There is one file for element %s"
							+ "(path is %s)",
					StructurizrUtils.getCanonicalPath(AADARCHI), elementRoot)); },
			// on multiple file detected
			(elementRoot, detectedFiles) -> { Assertions.fail(String.format(
					"There are more than one files for element %s"
							+ "(path is %s)",
					StructurizrUtils.getCanonicalPath(AADARCHI), elementRoot)); }
			);
	}
}
