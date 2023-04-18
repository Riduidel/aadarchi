package org.ndx.aadarchi.vfs.github;

import org.apache.commons.vfs2.*;
import org.apache.commons.vfs2.provider.AbstractOriginatingFileProvider;
import org.apache.commons.vfs2.util.UserAuthenticatorUtils;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

import java.io.IOException;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Optional;

import static org.apache.commons.vfs2.Capability.*;
import static org.apache.commons.vfs2.UserAuthenticationData.*;

public class GitHubFileProvider extends AbstractOriginatingFileProvider {
	static final Collection<Capability> CAPABILITIES = EnumSet.of(
//            CREATE,
			DELETE,
//            RENAME,
			GET_TYPE, LIST_CHILDREN, READ_CONTENT, GET_LAST_MODIFIED,
//            WRITE_CONTENT,
//            APPEND_CONTENT,
			URI);

	static final Type[] AUTHENTICATION_DATA_TYPES = { PASSWORD };

	public GitHubFileProvider() {
		super();
		this.setFileNameParser(new GitHubFileNameParser());
	}

	@Override
	public Collection<Capability> getCapabilities() {
		return CAPABILITIES;
	}

	@Override
	protected FileSystem doCreateFileSystem(FileName rootFileName, FileSystemOptions fileSystemOptions)
			throws FileSystemException {
		Optional<UserAuthenticationData> authentication = getAuthenticationData(fileSystemOptions);
		// Initiale GitHub API from authentication data
		return authentication
				.map(auth -> this.doCreateAuthFileSystem(rootFileName, fileSystemOptions, auth))
				.orElseThrow(() -> new UnsupportedOperationException("Can't create GitHub file system when there is no authentication data"));
	}

	private GitHubFileSystem doCreateAuthFileSystem(FileName rootFileName, FileSystemOptions fileSystemOptions,
			UserAuthenticationData auth) {
		String password = String.copyValueOf(auth.getData(PASSWORD));
		try {
			GitHub github = new GitHubBuilder().withOAuthToken(password).build();
			return new GitHubFileSystem(rootFileName, fileSystemOptions, github);
		} catch (IOException e) {
			throw new UnsupportedOperationException(
					String.format("Unable to create github file system from %s", rootFileName),
					e);
		}
	}

	private Optional<UserAuthenticationData> getAuthenticationData(FileSystemOptions fileSystemOptions) {
		UserAuthenticationData userAuthenticationData = UserAuthenticatorUtils.authenticate(fileSystemOptions,
				AUTHENTICATION_DATA_TYPES);
		return Optional.ofNullable(userAuthenticationData);
	}

	/**
	 * Convert the relative project url into a valid commons-vfs url (with the GitHub prefix)
	 * @param project user/project identifier. If absolute path is given as parameter, it is replaced.
	 * @return
	 */
	public static String urlFor(String project) {
		if(project.contains(".git")) {
			project = project.replace(".git", "");
		}
		if(project.startsWith("https://github.com")) {
			return project.replace("https://", "github://");
		}
		return String.format("github://github.com/%s", project);
	}
}
