package org.ndx.aadarchi.vfs.gitlab;

import java.io.InputStream;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.provider.AbstractFileObject;
import org.gitlab4j.api.GitLabApi;

public class GitLabFileObject extends AbstractFileObject<GitLabFileSystem> implements FileObject {

	private GitLabApi gitlab;

	public GitLabFileObject(GitLabFileName name, GitLabFileSystem gitHubFileSystem, GitLabApi gitlab) {
		super(name, gitHubFileSystem);
		this.gitlab = gitlab;
	}
	
	@Override
	protected long doGetContentSize() throws Exception {
		throw new UnsupportedOperationException("TODO Implement "+getClass().getSimpleName()+"#doGetContentSize()");
	}

	@Override
	protected FileType doGetType() throws Exception {
		throw new UnsupportedOperationException("TODO Implement "+getClass().getSimpleName()+"#doGetType()");
	}
	
	@Override
	protected InputStream doGetInputStream() throws Exception {
		throw new UnsupportedOperationException("TODO Implement "+getClass().getSimpleName()+"#doGetInputStream()");
	}
	
	@Override
	public GitLabFileName getName() {
		return (GitLabFileName) super.getName();
	}

	@Override
	protected String[] doListChildren() throws Exception {
		throw new UnsupportedOperationException("TODO Implement "+getClass().getSimpleName()+"#doListChildren()");
	}

}
