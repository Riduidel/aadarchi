package org.ndx.aadarchi.cdi.deltaspike.commons;

import java.nio.file.NoSuchFileException;

import org.apache.commons.configuration2.EnvironmentConfiguration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.SystemConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.io.ClasspathLocationStrategy;
import org.ndx.aadarchi.cdi.deltaspike.ConfigSource;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;

public class CommonsConfigurationConfigProducer {
	private static final String DELTASPIKE_PROPERTIES_PATH = "META-INF/apache-deltaspike.properties";
	@Produces @Dependent ConfigSource createDeltaspikePropertiesSource() throws ConfigurationException {
        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

        if(getClass().getClassLoader().getResource(DELTASPIKE_PROPERTIES_PATH)==null) {
        	return null;
        }
        
        Parameters params = new Parameters();
        FileBasedConfigurationBuilder<FileBasedConfiguration> builder =
                new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                        .configure(params.properties()
                        		.setLocationStrategy(new ClasspathLocationStrategy())
                                .setFileName(DELTASPIKE_PROPERTIES_PATH));
        FileBasedConfiguration configuration = builder.getConfiguration();
		
		return new CommonsConfigurationConfigSource("apache-deltaspike.properties", 10, configuration);
	}
	@Produces @Dependent ConfigSource createEnvironmentSource() {
		return new CommonsConfigurationConfigSource(
				"environment",
				0,
				new EnvironmentConfiguration());
	}
	@Produces @Dependent ConfigSource createPropertiesSource() {
		return new CommonsConfigurationConfigSource(
				"properties",
				100,
				new SystemConfiguration());
	}
}
