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

import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys;
import org.ndx.aadarchi.cdi.deltaspike.ConfigProvider;

import com.structurizr.Workspace;
import com.structurizr.model.StaticStructureElement;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class RelationshipDescriptionProvider {
	@Inject Logger logger;
	
	@Inject ConfigProvider configProvider;
	
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
		File propertiesFile = getDescriptionsPropertiesFile(workspace.getProperties().getOrDefault(
				ModelElementKeys.ConfigProperties.RelationshipNames.NAME,
				ModelElementKeys.ConfigProperties.RelationshipNames.VALUE));
		Properties returned = new Properties();
		if(propertiesFile.exists()) {
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
		} else {
			logger.log(Level.FINE,
					String.format("If you want to customize descriptions in %s,"
							+ " create file %s"
							+ "(keys are relationships - INPUT->OUTPUT - values are descriptions", 
							workspace.getName(),
							propertiesFile.getAbsolutePath()));
		}
		return new RelationshipFinder(returned);
	}

	private File getDescriptionsPropertiesFile(String relationshipPath) {
		String propertiesPath = configProvider.resolve(null, relationshipPath);
				;
		if(propertiesPath==null) {
			/*
			 * In some tests, this may not work (since the cdi-in-maven-plugin-help dependency is missing)
			 * As a consequence, we hard-code some replacement value - yup, that's bad
			 */
			try {
				propertiesPath = relationshipPath
						.replace("${project.basedir}", new File(".").getCanonicalPath());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		File propertiesFile = new File(propertiesPath);
		return propertiesFile;
	}
	
	public RelationshipFinder descriptionsIn(Workspace workspace) {
		return relationshipCache.computeIfAbsent(workspace, this::createRelationshipDescriptionFinder);
	}
}
