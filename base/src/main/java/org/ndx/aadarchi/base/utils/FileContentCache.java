package org.ndx.aadarchi.base.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.function.Function;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys.ConfigProperties.Force;
import org.ndx.aadarchi.base.enhancers.scm.SCMFile;

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
			defaultValue = ModelElementKeys.ConfigProperties.CacheDir.VALUE) File cacheDir;

	public InputStream openStreamFor(URL url, Function<URL, InputStream> cacheLoader) throws IOException {
		File file = toCacheFile(url);
		if(force || !file.exists() || shouldRefresh(file)) {
			refreshCache(file, url, cacheLoader);
		}
		// Now it's time to load file in cache
		return FileUtils.openInputStream(file);
	}
	public InputStream openStreamFor(String string, Function<URL, InputStream> cacheLoader) throws IOException {
		return openStreamFor(new URL(string), cacheLoader);
	}

	/**
	 * Get cached version of remote file
	 * @param file, remote scm file
	 * @return input stream to locally cached version of that file
	 * @throws IOException thrown if remote file can't be read
	 */
	public InputStream openStreamFor(SCMFile file) throws IOException {
		return openStreamFor(file.url(), _url -> file.content());
	}

	private void refreshCache(File file, URL url, Function<URL, InputStream> cacheLoader) throws IOException {
		file.getParentFile().mkdirs();
		Files.copy(cacheLoader.apply(url), file.toPath());
	}

	/**
	 * If file is more than 12 hours old, refresh it
	 * @param file
	 * @return
	 */
	private boolean shouldRefresh(File file) {
		return file.lastModified()<(System.currentTimeMillis()-1000*60*60*12);
	}

	/**
	 * Convert url to file by removing protocol then concatenating all segments below cache dir
	 * @param url url for which we want a cache key
	 * @return a cache file path
	 */
	private File toCacheFile(URL url) {
		File domain = new File(cacheDir, url.getHost());
		File path = new File(domain, url.getFile().replace('?', '_'));
		return path;
	}
}
