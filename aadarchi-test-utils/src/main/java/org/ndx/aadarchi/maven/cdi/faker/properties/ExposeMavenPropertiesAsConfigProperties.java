package org.ndx.aadarchi.maven.cdi.faker.properties;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.apache.deltaspike.core.api.config.Source;
import org.apache.deltaspike.core.api.projectstage.ProjectStage;
import org.apache.deltaspike.core.spi.config.ConfigSource;

@Source
public class ExposeMavenPropertiesAsConfigProperties implements ConfigSource {
	private static final Logger logger = Logger.getLogger(ExposeMavenPropertiesAsConfigProperties.class.getName());

	private static final int DELTASPIKE_PRIORITY = 1000;

	public ExposeMavenPropertiesAsConfigProperties() {
		super();
	}
	
	@Inject ProjectStage projectStage;

	@Override
	public String getConfigName() {
		return "maven-fake-properties";
	}

	@Override
	public Map<String, String> getProperties() {
		return null;
	}
	
	@Override
	public String getPropertyValue(String key) {
		switch(key) {
		case "project.basedir":
			return computeBasedir();
		case "project.build.directory":
			return computeBuildDirectory();
		default:
			return null;
		}
	}

	private String computeBuildDirectory() {
		return computePathMatching(file -> new File(file, "target").exists() && new File(file, "target").isDirectory())+"/target";
	}

	private String computeBasedir() {
		return computePathMatching(file -> new File(file, ".git").exists());
	}
	
	private String computePathMatching(Predicate<File> predicate) {
		File current = new File(".");
		try {
			current = current.getCanonicalFile();
			return computeParentPathMatching(current, predicate);
		} catch (IOException e) {
			throw new RuntimeException(String.format("Can't buid a canonical file for %s", current), e);
		}
	}

	private String computeParentPathMatching(File current, Predicate<File> test) {
		if(test.test(current))
			return current.getAbsolutePath();
		else 
			return computeParentPathMatching(current.getParentFile(), test);
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
