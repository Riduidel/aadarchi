package org.ndx.aadarchi.base.utils.descriptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.deltaspike.core.api.config.ConfigResolver;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys;

import com.structurizr.Workspace;
import com.structurizr.model.StaticStructureElement;

@ApplicationScoped
public class RelationshipDescriptionProvider {
	@Inject Logger logger;
	
	public class RelationshipFinder {

		private Properties descriptions;

		public RelationshipFinder(Properties returned) {
			this.descriptions = returned;
		}

		public <Contained extends StaticStructureElement> Optional<String> getDescriptionFor(Contained from,
				Contained to) {
			String key = String.format("%s->%s", from.getName(), to.getName());
			// First, get file location from the various model elements
			// Then 
			return Optional.ofNullable(descriptions.getProperty(key));
		}
		
	}
	
	private Map<Workspace, RelationshipFinder> relationshipCache = new HashMap<>();
	
	private RelationshipFinder createRelationshipDescriptionFinder(Workspace workspace) {
		String relationshipPath = workspace.getProperties().getOrDefault(
				ModelElementKeys.ConfigProperties.RelationshipNames.NAME,
				ModelElementKeys.ConfigProperties.RelationshipNames.VALUE);
		File propertiesFile = ConfigResolver.resolve(relationshipPath)
				.as(File.class)
				.getValue();
		Properties returned = new Properties();
		try {
			try(InputStream input = new FileInputStream(propertiesFile)) {
				returned.load(input);
			}
		} catch(IOException e) {
			logger.log(Level.WARNING,
					String.format("Unable to read descriptions.\nworkspace is %s.\nAssociated file is %s" , 
							workspace.getName(),
							propertiesFile.getAbsolutePath()),
					e);
		}
		return new RelationshipFinder(returned);
	}
	
	public RelationshipFinder descriptionsIn(Workspace workspace) {
		return relationshipCache.computeIfAbsent(workspace, this::createRelationshipDescriptionFinder);
	}
}
