package org.ndx.aadarchi.base.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.provider.local.LocalFile;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys.ConfigProperties.Force;
import org.ndx.aadarchi.cdi.deltaspike.ConfigProperty;

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

	/**
	 * Open stream from the given source.
	 * @param source
	 * @return
	 * @throws IOException
	 */
	public InputStream openStreamFor(FileObject source) throws IOException {
		// We shouldn't cache local files.
		// It's both inefficient, and triggers weird bugs on Windows
		if(source instanceof LocalFile) {
			return source.getContent().getInputStream();
		}
		// Sometimes this url uses custom protocols, which Java doesn't fully understand
		// So replace all non standard protocols by http
		String uri = source.getPublicURIString()
				.replace("github:", "http:")
				.replace("gitlab:", "http:")
				.replace("http5s:", "https:")
				.replace("http5:", "http:")
				;
		URL url = new URL(uri);
		FileObject file = toCacheFile(url);
		if(force || !file.exists() || shouldRefresh(file)) {
			refreshCache(file, url);
		}
		// Now it's time to load file in cache
		return file.getContent().getInputStream();
	}

	private void refreshCache(FileObject file, URL url) throws IOException {
		file.getParent().createFolder();
		try(InputStream input = url.openStream()) {
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
			String pathInUrl = url.getFile()
					.replace('?', '_')
					.replace(":", "__");
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
