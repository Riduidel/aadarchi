package org.ndx.aadarchi.vfs.gitlab;

import static org.apache.commons.vfs2.Capability.DELETE;
import static org.apache.commons.vfs2.Capability.GET_LAST_MODIFIED;
import static org.apache.commons.vfs2.Capability.GET_TYPE;
import static org.apache.commons.vfs2.Capability.LIST_CHILDREN;
import static org.apache.commons.vfs2.Capability.READ_CONTENT;
import static org.apache.commons.vfs2.Capability.URI;
import static org.apache.commons.vfs2.UserAuthenticationData.DOMAIN;
import static org.apache.commons.vfs2.UserAuthenticationData.PASSWORD;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Optional;

import org.apache.commons.vfs2.Capability;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileSystem;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.UserAuthenticationData;
import org.apache.commons.vfs2.UserAuthenticationData.Type;
import org.apache.commons.vfs2.provider.AbstractOriginatingFileProvider;
import org.apache.commons.vfs2.util.UserAuthenticatorUtils;
import org.gitlab4j.api.GitLabApi;

public class GitLabFileProvider extends AbstractOriginatingFileProvider {
	static final Collection<Capability> CAPABILITIES = EnumSet.of(
//            CREATE,
			DELETE,
//            RENAME,
			GET_TYPE, LIST_CHILDREN, READ_CONTENT, GET_LAST_MODIFIED,
//            WRITE_CONTENT,
//            APPEND_CONTENT,
			URI);

	static final Type[] AUTHENTICATION_DATA_TYPES = { DOMAIN, PASSWORD };

	public GitLabFileProvider() {
		super();
		this.setFileNameParser(new GitLabFileNameParser());
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

	private GitLabFileSystem doCreateAuthFileSystem(FileName rootFileName, FileSystemOptions fileSystemOptions,
			UserAuthenticationData auth) {
		String password = String.copyValueOf(auth.getData(PASSWORD));
		String domain = String.copyValueOf(auth.getData(DOMAIN));
		if(!domain.startsWith("http")) {
			domain = "https://"+domain;
		}
		GitLabApi gitlabApi = new GitLabApi(domain, password);
		return new GitLabFileSystem(rootFileName, fileSystemOptions, gitlabApi);
	}

	private Optional<UserAuthenticationData> getAuthenticationData(FileSystemOptions fileSystemOptions) {
		UserAuthenticationData userAuthenticationData = UserAuthenticatorUtils.authenticate(fileSystemOptions,
				AUTHENTICATION_DATA_TYPES);
		return Optional.ofNullable(userAuthenticationData);
	}

	public static String urlFor(String gitlabServer, String project) {
		return "gitlab://"+gitlabServer+"/"+project+GitLabFileName.PROJET_PATH_SEPARATOR;
	}
}
