package org.ndx.aadarchi.vfs.github;

import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.provider.FileNameParser;
import org.apache.commons.vfs2.provider.GenericURLFileName;
import org.apache.commons.vfs2.provider.GenericURLFileNameParser;
import org.apache.commons.vfs2.provider.VfsComponentContext;

public class GitHubFileNameParser extends GenericURLFileNameParser implements FileNameParser {

	public GitHubFileNameParser() {
		super(443);
	}

	@Override
	public GitHubFileName parseUri(VfsComponentContext context, FileName base, String fileName) throws FileSystemException {
		GenericURLFileName temporary = (GenericURLFileName) super.parseUri(context, base, fileName);
		String fullPath = temporary.getPath();
		String[] segments = fullPath.split("/");
		String gitHubUserOrOrganization = null;
		String repository =  null;
		if(base!=null && base instanceof GitHubFileName) {
			GitHubFileName githubBase = (GitHubFileName) base;
			gitHubUserOrOrganization = githubBase.getGithubUserOrOrganization();
			repository = githubBase.getRepository();
		} else {
			gitHubUserOrOrganization = segments.length>1 ? segments[1] : null;
			repository =  segments.length>2 ? segments[2] : null;
		}
		return new GitHubFileName(temporary.getScheme(), 
				temporary.getHostName(), 
				temporary.getPort(), temporary.getDefaultPort(), 
				temporary.getUserName(), temporary.getPassword(),
				gitHubUserOrOrganization, repository,
				fullPath,
				null /* can be loaded afterwards */,
				temporary.getQueryString());
	}
}
