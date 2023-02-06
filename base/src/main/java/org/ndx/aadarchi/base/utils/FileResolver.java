package org.ndx.aadarchi.base.utils;

import javax.inject.Inject;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.ndx.aadarchi.base.AgileArchitectureException;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys.ConfigProperties.BasePath;

/**
 * A utility class allowing resolving relative file paths to effective project
 * root
 * 
 * @author nicolas-delsaux
 *
 */
public class FileResolver {
	public static class UnableToResolveFileException extends AgileArchitectureException {

		public UnableToResolveFileException(String string, Exception e) {
			super(string, e);
		}

	}

	@Inject
	FileSystemManager fsManager;

	@Inject
	@ConfigProperty(name = BasePath.NAME, defaultValue = BasePath.VALUE)
	FileObject basedir;

	public FileObject fileAsUrltoPath(String file) {
		try {
			return fsManager.resolveFile(file);
		} catch (FileSystemException e) {
			try {
				return basedir.resolveFile(file);
			} catch (FileSystemException e1) {
				throw new UnableToResolveFileException(
						String.format("Unable to transform String %s into a file/path object", file), e);
			}
		}
	}
}
