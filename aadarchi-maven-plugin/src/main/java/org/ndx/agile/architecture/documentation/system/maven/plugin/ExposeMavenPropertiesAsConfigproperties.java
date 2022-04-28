package org.ndx.agile.architecture.documentation.system.maven.plugin;

import javax.inject.Inject;

import org.apache.deltaspike.core.api.config.Source;
import org.apache.deltaspike.core.impl.config.PropertiesConfigSource;
import org.apache.deltaspike.core.spi.config.ConfigSource;
import org.apache.maven.project.MavenProject;

@Source
public class ExposeMavenPropertiesAsConfigproperties extends PropertiesConfigSource implements ConfigSource {
	
	@Inject
	public ExposeMavenPropertiesAsConfigproperties(MavenProject mavenProject) {
        super(mavenProject.getProperties());
        initOrdinal(1000);
	}

	@Override
	public String getConfigName() {
		return "maven-properties";
	}
}
