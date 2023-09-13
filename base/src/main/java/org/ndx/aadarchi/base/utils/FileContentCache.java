package org.ndx.aadarchi.base.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.function.Function;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys.ConfigProperties.Force;

import com.pivovarit.function.ThrowingFunction;

/**
 * A specific file cache, usable to avoid downloading multiple times the same file content.
 * @author Nicolas
 *
 */
@com.structurizr.annotation.Component(technology = "Java, CDI")
@ApplicationScoped
public class FileContentCache {
	
	@Inject
	@ConfigProperty(name = Force.NAME, defaultValue=Force.VALUE)
	boolean force;
	@Inject @ConfigProperty(
			name = ModelElementKeys.ConfigProperties.CacheDir.NAME, 
			defaultValue = ModelElementKeys.ConfigProperties.CacheDir.VALUE) FileObject cacheDir;

	private InputStream openStreamFor(FileObject source, Function<URL, InputStream> function) throws IOException {
		// We shouldn't cache local files.
		// It's both inefficient, and triggers weird bugs on Windows
		if(source instanceof LocalFile) {
			
		}
		// Sometimes this url uses custom protocols, which Java doesn't fully understand
		// So replace all non standard protocols by http
		String uri = source.getPublicURIString()
				.replace("github:", "http:")
				.replace("gitlab:", "http:");
		URL url = new URL(uri);
		FileObject file = toCacheFile(url);
		if(force || !file.exists() || shouldRefresh(file)) {
			refreshCache(file, url, function);
		}
		// Now it's time to load file in cache
		return file.getContent().getInputStream();
	}

	/**
	 * Get cached version of remote file
	 * @param file, remote scm file
	 * @return input stream to locally cached version of that file
	 * @throws IOException thrown if remote file can't be read
	 */
	public InputStream openStreamFor(FileObject file) throws IOException {
		try {
			return openStreamFor(
					file, 
					ThrowingFunction.unchecked(_url -> file.getContent().getInputStream()));
		} finally {
			file.close();
		}
	}

	private void refreshCache(FileObject file, URL url, Function<URL, InputStream> cacheLoader) throws IOException {
		file.getParent().createFolder();
		try(InputStream input = cacheLoader.apply(url)) {
			try(OutputStream output = file.getContent().getOutputStream()) {
				IOUtils.copy(input, output);
			} finally {
				file.getContent().close();
			}
		}
	}

	/**
	 * If file is more than 12 hours old, refresh it
	 * @param file
	 * @return
	 */
	private boolean shouldRefresh(FileObject file) {
		try {
			return file.getContent().getLastModifiedTime() <(System.currentTimeMillis()-1000*60*60*12);
		} catch (FileSystemException e) {
			throw new CantAccessPath(
					String.format("Unable to access path for %s to get last modified", file), e);
		}
	}

	/**
	 * Convert url to file by removing protocol then concatenating all segments below cache dir
	 * @param url url for which we want a cache key
	 * @return a cache file path
	 */
	private FileObject toCacheFile(URL url) {
		try {
			FileObject domain = cacheDir.resolveFile(url.getHost());
			String pathInUrl = url.getFile().replace('?', '_');
			// Don't forget that linux systems don't like when paths start with "/"
			if(pathInUrl.startsWith("/"))
				pathInUrl = pathInUrl.substring(1);
			FileObject path = domain.resolveFile(pathInUrl);
			return path;
		} catch(FileSystemException e) {
			throw new CantToResolvePath(
					String.format("Unable to construct path for %s", url), e);
		}
	}
}
