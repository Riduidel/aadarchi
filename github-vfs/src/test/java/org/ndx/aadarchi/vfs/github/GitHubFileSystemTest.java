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

class GitHubFileSystemTest {
	private static FileSystemOptions authenticationOptions;

	@BeforeAll public static void putGitHubCredentialsInAuth() {
		String token = System.getProperty("aadarchi.github.token");
		StaticUserAuthenticator auth = new StaticUserAuthenticator("github.com", 
				null, token);
		authenticationOptions = new FileSystemOptions();
		DefaultFileSystemConfigBuilder.getInstance().setUserAuthenticator(authenticationOptions, auth);
	}

	@Test
	void cannot_connect_to_this_project_on_github_without_authentication() throws FileSystemException {
		org.junit.jupiter.api.Assertions.assertThrows(UnsupportedOperationException.class, 
				() -> VFS.getManager().resolveFile(GitHubFileProvider.urlFor("Riduidel/aadarchi")));
	}

	@Test
	void can_connect_to_this_project_on_github_with_authentication() throws FileSystemException {
		FileObject rootFile = VFS.getManager().resolveFile(GitHubFileProvider.urlFor("Riduidel/aadarchi"), authenticationOptions);
		Assertions.assertThat((Object) rootFile).isNotNull();
	}

	@Test
	void can_list_project_root_children() throws FileSystemException {
		FileObject rootFile = VFS.getManager().resolveFile(GitHubFileProvider.urlFor("Riduidel/aadarchi"), authenticationOptions);
		FileObject[] children = rootFile.getChildren();
		Assertions.assertThat(children).hasSizeGreaterThan(3);
	}

	@Test
	void can_read_readme_content() throws IOException {
		FileObject rootFile = VFS.getManager().resolveFile(GitHubFileProvider.urlFor("Riduidel/aadarchi"), authenticationOptions);
		FileObject readme = rootFile.getChild("README.md");
		Assertions.assertThat(readme.getContent().getSize()).isGreaterThan(100);
		Assertions.assertThat(readme.getContent().getString(Charset.forName("UTF-8")))
			.contains("mvn", "Aadarchi", "Nicolas Delsaux");
	}
}
