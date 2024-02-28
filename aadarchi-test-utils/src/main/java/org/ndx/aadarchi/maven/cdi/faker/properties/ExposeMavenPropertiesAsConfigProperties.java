package org.ndx.aadarchi.maven.cdi.faker.properties;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Logger;

import org.ndx.aadarchi.cdi.deltaspike.ConfigSource;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ExposeMavenPropertiesAsConfigProperties implements ConfigSource {
	private static final Logger logger = Logger.getLogger(ExposeMavenPropertiesAsConfigProperties.class.getName());

	private static final int DELTASPIKE_PRIORITY = 1000;
	
	public static enum Keys {
		BASE_DIR("project.basedir") {

			@Override
			public String getComputedValue() {
				return computeBasedir();
			}
			
		},
		BUILD_DIR("project.build.directory") {

			@Override
			public String getComputedValue() {
				return computeBuildDirectory();
			}
			
		};

		private final String propertyKey;

		private Keys(String propertyKey) {
			this.propertyKey = propertyKey;
		}

		static Optional<String> getEnumValueFor(String key) {
			for(Keys k : values()) {
				if(k.getPropertyKey().equals(key)) {
					return Optional.ofNullable(k.getComputedValue());
				}
			}
			return Optional.empty();
		}

		String getPropertyKey() {
			return propertyKey;
		}

		public abstract String getComputedValue();
		
	}

	public ExposeMavenPropertiesAsConfigProperties() {
		super();
	}
	
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
		return Keys.getEnumValueFor(key)
			.orElse(null);
	}

	static String computeBuildDirectory() {
		return computePathMatching(file -> new File(file, "target").exists() && new File(file, "target").isDirectory())+"/target";
	}

	static String computeBasedir() {
		return computePathMatching(file -> new File(file, ".git").exists());
	}
	
	static private String computePathMatching(Predicate<File> predicate) {
		File current = new File(".");
		try {
			current = current.getCanonicalFile();
			return computeParentPathMatching(current, predicate);
		} catch (IOException e) {
			throw new RuntimeException(String.format("Can't buid a canonical file for %s", current), e);
		}
	}

	static private String computeParentPathMatching(File current, Predicate<File> test) {
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
