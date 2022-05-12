package org.ndx.agile.architecture.documentation.system.maven.plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.inject.Inject;

import org.apache.deltaspike.core.api.config.Source;
import org.apache.deltaspike.core.spi.config.ConfigSource;
import org.apache.maven.project.MavenProject;

@Source
public class ExposeMavenPropertiesAsConfigproperties implements ConfigSource {
	private static final Logger logger = Logger.getLogger(ExposeMavenPropertiesAsConfigproperties.class.getName());

	private static final int DELTASPIKE_PRIORITY = 1000;

	@Inject
	MavenProject mavenProject;

	private Map<String, String> properties = new HashMap();

	public ExposeMavenPropertiesAsConfigproperties() {
		super();
	}

	@Override
	public String getConfigName() {
		return "maven-properties";
	}

	@PostConstruct
	public void initialize() {
		logger.info("Initializing maven properties exposer");
		properties = initProperties(mavenProject);
		logger.info(String.format("Loaded a total of %d properties", properties.size()));
	}

	@Override
	public Map<String, String> getProperties() {
		return properties;
	}

	private Map<String, String> initProperties(MavenProject project) {
		Map<String, String> returned = new TreeMap<String, String>();
		returned.putAll(toMap(project.getProperties()));
		// Mind you, maven properties not only include maven properties, but also build
		// properties (project.version, and so on)
		// So we're gonna find them, and we're gonna add them
		// the simplest possible solution is, just, to, well, rewrite them
		returned.put("project.basedir", project.getBasedir().getAbsolutePath());
		returned.put("project.baseUri", project.getBasedir().toURI().toString());
		returned.put("project.version", project.getVersion());
		returned.put("project.build.directory", project.getBuild().getDirectory());
		returned.put("project.build.outputDirectory", project.getBuild().getOutputDirectory());
		returned.put("project.name", project.getName());
		returned.put("project.version", project.getVersion());
		returned.put("project.groupId", project.getGroupId());
		returned.put("project.artifactId", project.getArtifactId());
		returned.put("project.description", project.getDescription());
		returned.put("project.build.finalName", project.getBuild().getFinalName());
		return returned;
	}

	private static Map<String, String> toMap(Properties properties2) {
		Map<String, String> returned = new HashMap<String, String>();
		properties2.entrySet().forEach(entry -> returned.put(entry.getKey().toString(), entry.getValue().toString()));
		return returned;
	}

	@Override
	public String getPropertyValue(String key) {
		return properties.get(key);
	}

	@Override
	public boolean isScannable() {
		return true;
	}

	@Override
	public int getOrdinal() {
		return DELTASPIKE_PRIORITY;
	}
}
