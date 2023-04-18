package org.ndx.aadarchi.vfs.gitlab;

import java.util.Collection;
import java.util.List;

import org.apache.commons.vfs2.Capability;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.provider.AbstractFileName;
import org.apache.commons.vfs2.provider.AbstractFileSystem;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.models.Project;

public class GitLabFileSystem extends AbstractFileSystem {

	private GitLabApi gitlab;

	public GitLabFileSystem(FileName rootFileName, FileSystemOptions fileSystemOptions, GitLabApi gitlab) {
		super(rootFileName, null, fileSystemOptions);
		this.gitlab = gitlab;
	}

	@Override
	protected FileObject createFile(AbstractFileName name) throws Exception {
		GitLabFileName filename  = (GitLabFileName) name;
		if(filename.getProjectObject()==null) {
			Project project = gitlab.getProjectApi().getProject(filename.getNamespace(), filename.getProject());
			filename.setProjectObject(project);
		}
		if(filename.getBranch()==null) {
			filename.setBranch(filename.getProjectObject().getDefaultBranch());
		}
		return new GitLabFileObject(filename, this, gitlab);
	}

	@Override
	protected void addCapabilities(Collection<Capability> caps) {
		caps.addAll(GitLabFileProvider.CAPABILITIES);
	}

}
