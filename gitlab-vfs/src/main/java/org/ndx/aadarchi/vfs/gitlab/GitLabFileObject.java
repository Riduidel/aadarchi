package org.ndx.aadarchi.vfs.gitlab;

import java.io.InputStream;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.provider.AbstractFileObject;

public class GitLabFileObject extends AbstractFileObject<GitLabFileSystem> implements FileObject {

	public GitLabFileObject(GitLabFileName name, GitLabFileSystem gitHubFileSystem) {
		super(name, gitHubFileSystem);
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
