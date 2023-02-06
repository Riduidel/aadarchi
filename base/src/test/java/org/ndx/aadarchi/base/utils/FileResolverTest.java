package org.ndx.aadarchi.base.utils;

import javax.inject.Inject;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.assertj.core.api.AssertionsForClassTypes;
import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@EnableWeld
class FileResolverTest {

    @WeldSetup
    public WeldInitiator weld = WeldInitiator.performDefaultDiscovery();
    @Inject FileSystemManager fsManager;
	@Inject FileResolver resolver;
    
    /**
     * We build this without using Weld, since this unit test doesn't access any maven property
     * (typically the basedir can't be resolved)
     * @throws FileSystemException 
     */
    @BeforeEach public void loadFileResolver() throws FileSystemException {
    }

	@Test
	void can_resolve_subfolder() throws FileSystemException {
		// Given
		// When
		FileObject resolved = resolver.fileAsUrltoPath("src/main/java");
		// Then
		AssertionsForClassTypes.assertThat(resolved).isEqualTo(resolver.basedir.resolveFile("src/main/java"));
	}
	@Test
	void bug_291_absolute_path_cant_be_resolved() throws FileSystemException {
		// Given
		String userDir = System.getProperty("user.dir");
		// When
		FileObject resolved = resolver.fileAsUrltoPath(userDir);
		// Then
		AssertionsForClassTypes.assertThat(resolved).isEqualTo(fsManager.resolveFile(userDir));
	}

}
