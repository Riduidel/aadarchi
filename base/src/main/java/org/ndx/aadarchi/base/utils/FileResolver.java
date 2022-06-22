package org.ndx.aadarchi.base.utils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.inject.Inject;

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
	@ConfigProperty(name = BasePath.NAME, defaultValue = BasePath.VALUE)
	File basedir;

	public Path fileAsUrltoPath(String file) {
		try {
			if (file.startsWith("file:")) {
				return Paths.get(new URL(file).toURI());
			} else {
				return new File(basedir, file).toPath();
			}
		} catch (MalformedURLException | URISyntaxException e) {
			throw new UnableToResolveFileException(String.format("Unable to transform String %s into a file/path object", file), e);
		}
	}
}
