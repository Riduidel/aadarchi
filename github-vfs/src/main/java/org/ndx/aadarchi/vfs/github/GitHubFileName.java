package org.ndx.aadarchi.vfs.github;

import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.provider.GenericURLFileName;
import org.kohsuke.github.GHContent;

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

	/**
	 * Constructs a child from a parent and a child specification
	 * @param name
	 * @param childDescription
	 */
	public GitHubFileName(GitHubFileName name, GHContent childDescription) {
		super(name.getScheme(), name.getHostName(), name.getPort(), name.getDefaultPort(), 
				name.getUserName(), name.getPassword(),
				name.getPath()+"/"+childDescription.getPath(), 
				childDescription.getType().equals("file") ? FileType.FILE : FileType.FOLDER, 
				name.getQueryString());
		this.githubUserOrOrganization = name.githubUserOrOrganization;
		this.repository = name.repository;
		this.pathInRepository = childDescription.getPath();
		
	}

	public String getPathInRepository() {
		return pathInRepository;
	}

	public String getContainingRepository() {
		return githubUserOrOrganization+"/"+repository;
	}
}
