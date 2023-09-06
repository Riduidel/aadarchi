package org.ndx.aadarchi.inferer.maven.technologies;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MvnRepositoryArtifactsProducer {
	private static final Logger logger = Logger.getLogger(MvnRepositoryArtifactsProducer.class.getName());
	public static final String MVNREPOSITORY_ARTIFACTS = "mvnRepositoryArtifactsMap";
	
	/**
	 * @see https://stleary.github.io/JSON-java/index.html
	 * @return a JSONObject containing all popular artifacts
	 * @throws IOException
	 */
	@Produces @ApplicationScoped @Named(MVNREPOSITORY_ARTIFACTS) Map<String, MvnRepositoryArtifact> createMvnRepositoryArtifacts() throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		logger.info(
				String.format("Loading mvnrepository popular artifacts from %s", 
						getClass().getClassLoader().getResource("mvnrepository.json")));
		try(InputStream input = getClass().getClassLoader().getResourceAsStream("mvnrepository.json")) {
			return objectMapper.readValue(input, new TypeReference<Map<String, MvnRepositoryArtifact>>() {});
		}
	}
}
