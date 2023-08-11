package org.ndx.aadarchi.inferer.maven;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

public class MvnRepositoryArtifactsProducer {
	private static final Logger logger = Logger.getLogger(MvnRepositoryArtifactsProducer.class.getName());
	public static final String MVNREPOSITORY_ARTIFACTS = "mvnRepositoryArtifactsMap";
	/**
	 * @see https://stleary.github.io/JSON-java/index.html
	 * @return a JSONObject containing all popular artifacts
	 * @throws IOException
	 */
	@Produces @ApplicationScoped @Named(MVNREPOSITORY_ARTIFACTS) JSONObject createMvnRepositoryArtifacts() throws IOException {
		logger.info(
				String.format("Loading mvnrepository popular artifacts from %s", 
						getClass().getClassLoader().getResource("mvnrepository.json")));
		try(InputStream input = getClass().getClassLoader().getResourceAsStream("mvnrepository.json")) {
			String text = IOUtils.toString(input, "UTF-8");
			return new JSONObject(text);
		}
	}
}
