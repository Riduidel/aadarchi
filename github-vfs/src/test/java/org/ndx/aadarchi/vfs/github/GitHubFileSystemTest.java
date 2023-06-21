package org.ndx.aadarchi.vfs.github;

import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.vfs2.FileFilter;
import org.apache.commons.vfs2.FileFilterSelector;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSelectInfo;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.PatternFileSelector;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.auth.StaticUserAuthenticator;
import org.apache.commons.vfs2.filter.RegexFileFilter;
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
	void a_github_folder_has_the_vfs_folder_type() throws FileSystemException {
		FileObject rootFile = VFS.getManager().resolveFile(GitHubFileProvider.urlFor("Riduidel/aadarchi"), authenticationOptions);
		Assertions.assertThat(rootFile.getType()).isNotEqualTo(FileType.FILE);
	}

	@Test
	void can_read_readme_content() throws IOException {
		FileObject rootFile = VFS.getManager().resolveFile(GitHubFileProvider.urlFor("Riduidel/aadarchi"), authenticationOptions);
		FileObject readme = rootFile.getChild("README.md");
		// File type is not known before calling getContent
		Assertions.assertThat(rootFile.getType()).isEqualTo(FileType.FOLDER);
		Assertions.assertThat(readme.getType()).isEqualTo(FileType.FILE);
		Assertions.assertThat(readme.getContent().getSize()).isGreaterThan(100);
		Assertions.assertThat(readme.getContent().getString(Charset.forName("UTF-8")))
			.contains("mvn", "Aadarchi", "Nicolas Delsaux");
	}

	@Test
	void can_find_readme_in_root_folder() throws IOException {
		FileObject rootFile = VFS.getManager().resolveFile(GitHubFileProvider.urlFor("Riduidel/aadarchi"), authenticationOptions);
		FileObject[] files = rootFile.findFiles(new FileFilterSelector(new RegexFileFilter("README\\..*")));
		Assertions.assertThat(files).hasSize(1);
		FileObject readme = files[0];
		// Seems like when files are found below root, their public URI is quite some mess
		Assertions.assertThat(readme.getPublicURIString()).contains("github://github.com/");
	}

	@Test
	void can_find_readme_in_directly_located_subfolder() throws IOException {
		FileObject rootFile = VFS.getManager().resolveFile(GitHubFileProvider.urlFor("Riduidel/aadarchi/base"), authenticationOptions);
		FileObject[] files = rootFile.findFiles(new FileFilterSelector(new RegexFileFilter("README\\..*")));
		Assertions.assertThat(files).hasSize(1);
		FileObject readme = files[0];
		// Seems like when files are found below root, their public URI is quite some mess
		Assertions.assertThat(readme.getPublicURIString()).contains("github://github.com/");
	}

	@Test
	void can_find_readme_in_indirectly_located_subfolder() throws IOException {
		FileObject rootFile = VFS.getManager().resolveFile(GitHubFileProvider.urlFor("Riduidel/aadarchi"), authenticationOptions);
		FileObject baseFolder = rootFile.getChild("base");
		FileObject[] files = baseFolder.findFiles(new FileFilterSelector(new RegexFileFilter("README\\..*")));
		Assertions.assertThat(files).hasSize(1);
		FileObject readme = files[0];
		// Seems like when files are found below root, their public URI is quite some mess
		Assertions.assertThat(readme.getPublicURIString()).contains("github://github.com/");
	}
}
