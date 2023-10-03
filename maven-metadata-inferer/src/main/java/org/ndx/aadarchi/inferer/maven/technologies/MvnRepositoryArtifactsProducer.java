package org.ndx.aadarchi.inferer.maven.technologies;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.VFS;
import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys.ConfigProperties.EnhancementsDir;
import org.ndx.aadarchi.base.utils.FileContentCache;
import org.ndx.aadarchi.inferer.maven.MavenEnhancer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MvnRepositoryArtifactsProducer {
	private static final Logger logger = Logger.getLogger(MvnRepositoryArtifactsProducer.class.getName());
	public static final String MVNREPOSITORY_ARTIFACTS = "mvnRepositoryArtifactsMap";
	
	@Inject @ConfigProperty(
			name = "aadarchi.technologies.default", 
			defaultValue = "res://mvnrepository.json") FileObject defaultAadarchiTechnlogies;

	@Inject @ConfigProperty(name="aadarchi.technologies.up-to-date.url") FileObject upToDateAadarchiTechnologies;
	
	@Inject FileContentCache cache;

	/**
	 * @see https://stleary.github.io/JSON-java/index.html
	 * @return a JSONObject containing all popular artifacts
	 * @throws IOException
	 */
	@Produces @ApplicationScoped @Named(MVNREPOSITORY_ARTIFACTS) Map<String, MvnRepositoryArtifact> 
		createMvnRepositoryArtifacts() throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		// First, ensure the cached file has some content
		try {
			try(InputStream input = cache.openStreamFor(upToDateAadarchiTechnologies)) {
				return objectMapper.readValue(input, new TypeReference<Map<String, MvnRepositoryArtifact>>() {});
			}
		} catch(Exception e) {
			try(InputStream input = cache.openStreamFor(defaultAadarchiTechnlogies)) {
				return objectMapper.readValue(input, new TypeReference<Map<String, MvnRepositoryArtifact>>() {});
			}
		}
	}
}
