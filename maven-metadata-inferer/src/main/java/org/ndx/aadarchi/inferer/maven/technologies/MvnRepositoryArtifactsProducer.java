package org.ndx.aadarchi.inferer.maven.technologies;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.vfs2.FileObject;
import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.ndx.aadarchi.base.utils.FileContentCache;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
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
		List<MvnRepositoryArtifact> artifacts = readMvnRepositoryArtifacts(objectMapper);
		return artifacts.stream()
				.collect(Collectors.toMap(a -> a.coordinates, 
						a -> a,
						(a, b) -> a,
						() -> new TreeMap<String, MvnRepositoryArtifact>()));
	}

	private List<MvnRepositoryArtifact> readMvnRepositoryArtifacts(ObjectMapper objectMapper)
			throws IOException, StreamReadException, DatabindException {
		List<MvnRepositoryArtifact> artifacts = null;
		// First, ensure the cached file has some content
		try {
			try(InputStream input = cache.openStreamFor(upToDateAadarchiTechnologies)) {
				artifacts = objectMapper.readValue(input, new TypeReference<List<MvnRepositoryArtifact>>() {});
			} finally {
				upToDateAadarchiTechnologies.close();
			}
		} catch(Exception e) {
			logger.log(Level.WARNING, "Unable to read remote mvnrepository.json file", e);
			try(InputStream input = cache.openStreamFor(defaultAadarchiTechnlogies)) {
				artifacts = objectMapper.readValue(input, new TypeReference<List<MvnRepositoryArtifact>>() {});
			} finally {
				defaultAadarchiTechnlogies.close();
			}
		}
		return artifacts;
	}
}
