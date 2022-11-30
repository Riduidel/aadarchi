package org.ndx.aadarchi.base.utils;

import java.io.File;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.assertj.core.api.Assertions;

class FileResolverTest {

	@Test
	void bug_291_absolute_path_cant_be_resolved() {
		FileResolver resolver = new FileResolver();
		// Given
		// This file resolves as project base execution dir 
		String userDir = System.getProperty("user.dir");
		File userFile = new File(userDir);
		resolver.basedir = new File(userDir);
		// When
		Path resolved = resolver.fileAsUrltoPath(userDir);
		// Then
		Assertions.assertThat(resolved).isEqualTo(userFile.toPath());
	}

}
