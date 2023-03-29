package org.ndx.aadarchi.vfs.gitlab;

import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.provider.GenericURLFileName;

public class GitLabFileName extends GenericURLFileName implements FileName {

	public GitLabFileName(String scheme, String hostName, int port, int defaultPort, String userName, String password,
			String path, FileType type, String queryString) {
		super(scheme, hostName, port, defaultPort, userName, password, path, type, queryString);
	}
}
