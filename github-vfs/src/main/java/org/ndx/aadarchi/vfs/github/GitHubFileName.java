package org.ndx.aadarchi.vfs.github;

import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.provider.GenericURLFileName;

public class GitHubFileName extends GenericURLFileName implements FileName {

	private String repository;
	public String getRepository() {
		return repository;
	}

	public String getGithubUserOrOrganization() {
		return githubUserOrOrganization;
	}

	private String githubUserOrOrganization;
	private String pathInRepository = "";

	public GitHubFileName(String scheme, String hostName, int port, int defaultPort, String userName, String password,
			String gitHubUserOrOrganization, String repository, String path, FileType type, String queryString) {
		super(scheme, hostName, port, defaultPort, userName, password, path, type, queryString);
		this.githubUserOrOrganization = gitHubUserOrOrganization;
		this.repository = repository;
		if(!path.endsWith(getContainingRepository())) {
			int repositoryInPath = path.lastIndexOf(getContainingRepository());
			if(repositoryInPath>=0) {
				this.pathInRepository = path.substring(repositoryInPath+getContainingRepository().length());
			}
		}
	}

	public String getPathInRepository() {
		return pathInRepository;
	}

	public String getContainingRepository() {
		return githubUserOrOrganization+"/"+repository;
	}
}
