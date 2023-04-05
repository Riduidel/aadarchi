package org.ndx.aadarchi.vfs.gitlab;

import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.provider.GenericURLFileName;
import org.gitlab4j.api.models.Project;

public class GitLabFileName extends GenericURLFileName implements FileName {

	static final String PROJET_PATH_SEPARATOR = "/-";
	private String projectName;
	private String pathInRepository;
	private String namespace;
	private String branch;
	private Project projectObject;

	public GitLabFileName(String scheme, String hostName, int port, int defaultPort, String userName, String password,
			String path, FileType type, String queryString) {
		super(scheme, hostName, port, defaultPort, userName, password, path, type, queryString);
		int projectPathSeparator = getPath().indexOf(PROJET_PATH_SEPARATOR);
		if(projectPathSeparator>0) {
			String fullProject = getPath().substring(1, projectPathSeparator);
			int firstSlash = fullProject.indexOf('/');
			if(firstSlash>=0) {
				namespace = fullProject.substring(0, firstSlash);
				projectName = fullProject.substring(firstSlash+1);
			}
			pathInRepository = getPath().substring(projectPathSeparator+PROJET_PATH_SEPARATOR.length());
			if(pathInRepository.startsWith("/")) {
				pathInRepository = pathInRepository.substring(1);
			}
		} else {
			projectName = getPath().substring(1);
			pathInRepository = "";
		}
	}

	public String getProject() {
		return projectName;
	}

	public String getPathInRepository() {
		return pathInRepository;
	}

	public String getNamespace() {
		return namespace;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public Project getProjectObject() {
		return projectObject;
	}

	public void setProjectObject(Project project) {
		this.projectObject = project;
	}
}
