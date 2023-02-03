package org.ndx.aadarchi.cdi;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;

public class CommonsVfsFactoryProducer {
	@Produces @Singleton public FileSystemManager produceCommonsVfsFactory() throws FileSystemException {
		return VFS.getManager();
	}
}
