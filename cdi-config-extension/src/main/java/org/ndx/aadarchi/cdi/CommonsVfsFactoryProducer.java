package org.ndx.aadarchi.cdi;

import java.io.File;
import java.io.IOException;

import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.impl.DefaultFileSystemManager;

public class CommonsVfsFactoryProducer {
	@Produces @Singleton public FileSystemManager produceCommonsVfsFactory() throws IOException {
		FileSystemManager fileSystemManager = VFS.getManager();
		if (fileSystemManager instanceof DefaultFileSystemManager) {
			DefaultFileSystemManager configurable = (DefaultFileSystemManager) fileSystemManager;
			configurable.setBaseFile(new File(".").getCanonicalFile());
		}
		return fileSystemManager;
	}
}
