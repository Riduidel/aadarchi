package org.ndx.aadarchi.base.utils.descriptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.deltaspike.core.api.config.Config;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys;
import org.ndx.aadarchi.base.utils.StructurizrUtils;

import com.structurizr.PropertyHolder;
import com.structurizr.Workspace;
import com.structurizr.model.StaticStructureElement;

@ApplicationScoped
public class RelationshipDescriptionProvider {
	@Inject Logger logger;
	@Inject Config config;

	public <Contained extends StaticStructureElement> Optional<String> provideRelationshipDescription(Workspace workspace, Contained from,
			Contained to) {
		try {
			String key = String.format("%s->%s", from.getName(), to.getName());
			// First, get file location from the various model elements
			Properties knownRelationships = loadKnownProperties(Arrays.asList(workspace, from, to));
			// Then 
			return Optional.ofNullable(knownRelationships.getProperty(key));
		} catch(Exception e) {
			logger.log(Level.WARNING, String.format("Unable to load relationship description between %s and %s", from.getCanonicalName(), to.getCanonicalName()));
		}
		return Optional.empty();
	}

	private Properties loadKnownProperties(List<? extends PropertyHolder> asList) throws IOException {
		String propertiesPath = StructurizrUtils.getHierarchy(asList)
				.stream()
				.filter(propertyHolder -> propertyHolder.getProperties()
						.containsKey(ModelElementKeys.ConfigProperties.RelationshipNames.NAME))
				.map(propertyHolder -> propertyHolder.getProperties()
						.get(ModelElementKeys.ConfigProperties.RelationshipNames.NAME))
				.findFirst()
				.orElse(ModelElementKeys.ConfigProperties.RelationshipNames.NAME);
		File propertiesFile = config.resolve(propertiesPath)
			.as(File.class)
			.getValue();
		Properties returned = new Properties();
		try(InputStream input = new FileInputStream(propertiesFile)) {
			returned.load(input);
		}
		return returned;
	}

}
