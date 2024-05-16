package org.ndx.aadarchi.vfs.github;

import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.auth.StaticUserAuthenticator;
import org.apache.commons.vfs2.impl.DefaultFileSystemConfigBuilder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class TestFor345_Fail_When_Using_Repository_Path {
	private static FileSystemOptions authenticationOptions;

	@BeforeAll public static void putGitHubCredentialsInAuth() {
		String token = System.getProperty("aadarchi.github.token");
		StaticUserAuthenticator auth = new StaticUserAuthenticator("github.com", 
				null, token);
		authenticationOptions = new FileSystemOptions();
		DefaultFileSystemConfigBuilder.getInstance().setUserAuthenticator(authenticationOptions, auth);
	}

	@Test
	void can_get_file_when_calling_vfs() throws FileSystemException {
		FileObject rootFile = VFS.getManager().resolveFile(GitHubFileProvider.urlFor("https://github.com/Riduidel/aadarchi.git"), authenticationOptions);
		Assertions.assertThat((Object) rootFile).isNotNull();
	}
}
