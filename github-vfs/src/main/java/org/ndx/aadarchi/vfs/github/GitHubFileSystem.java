package org.ndx.aadarchi.vfs.github;

import java.util.Collection;

import org.apache.commons.vfs2.Capability;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.provider.AbstractFileName;
import org.apache.commons.vfs2.provider.AbstractFileSystem;
import org.apache.commons.vfs2.provider.GenericURLFileName;
import org.kohsuke.github.GitHub;

public class GitHubFileSystem extends AbstractFileSystem {

	private GitHub github;

	public GitHubFileSystem(FileName rootFileName, FileSystemOptions fileSystemOptions, GitHub github) {
		super(rootFileName, null, fileSystemOptions);
		this.github = github;
	}

	@Override
	protected FileObject createFile(AbstractFileName name) throws Exception {
		GitHubFileName filename  = (GitHubFileName) name;
		return new GitHubFileObject(filename, this, 
				github.getRepository(filename.getContainingRepository()));
	}

	@Override
	protected void addCapabilities(Collection<Capability> caps) {
		caps.addAll(GitHubFileProvider.CAPABILITIES);
	}

}
