package org.ndx.aadarchi.vfs.gitlab;

import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.provider.FileNameParser;
import org.apache.commons.vfs2.provider.GenericURLFileName;
import org.apache.commons.vfs2.provider.GenericURLFileNameParser;
import org.apache.commons.vfs2.provider.VfsComponentContext;

public class GitLabFileNameParser extends GenericURLFileNameParser implements FileNameParser {

	public GitLabFileNameParser() {
		super(443);
	}

	@Override
	public FileName parseUri(VfsComponentContext context, FileName base, String fileName) throws FileSystemException {
		GenericURLFileName temporary = (GenericURLFileName) super.parseUri(context, base, fileName);
		return new GitLabFileName(temporary.getScheme(), 
				temporary.getHostName(), 
				temporary.getPort(), temporary.getDefaultPort(), 
				temporary.getUserName(), temporary.getPassword(),
				temporary.getPath(),
				temporary.getType(),
				temporary.getQueryString());
	}
}
