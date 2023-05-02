package org.ndx.aadarchi.inferer.maven.enhancers;

import java.util.Collection;
import java.util.Map;

import com.structurizr.model.Relationship;
import org.apache.maven.project.MavenProject;
import org.ndx.aadarchi.base.relationships.RelationshipsPropertiesFileReader;
import org.ndx.aadarchi.inferer.maven.MavenPomDecorator;
import org.ndx.aadarchi.inferer.maven.MavenPomReader;

import com.structurizr.model.Container;
import com.structurizr.model.SoftwareSystem;

public class SoftwareSystemEnhancer extends AbstractContainerEnhancer<SoftwareSystem, Container> {

	public SoftwareSystemEnhancer(MavenPomReader mavenPomReader, SoftwareSystem softwareSystem) {
		super(mavenPomReader, softwareSystem);
	}
	@Override
	protected Container addContainedElementWithKey(MavenProject module, String key) {
		return enhanced.addContainer(key, module.getDescription(), MavenPomDecorator.decorateTechnology(module));
	}

	@Override
	protected Container getContainedElementWithName(String key) {
		return enhanced.getContainerWithName(key);
	}

	@Override
	protected String changeRelationshipDescription(Relationship uses) {
		String relationshipDescription = null;
		RelationshipsPropertiesFileReader propertiesFileReader = new RelationshipsPropertiesFileReader();
		Map<String, String> relationshipDescriptionProperties = propertiesFileReader.readPropertiesFile("base/src/architecture/resources/relationships-description.properties");
		for (Map.Entry<String, String> entry: relationshipDescriptionProperties.entrySet()) {
			String relationshipDescriptionProperty = uses.getSource().getName()+"->"+uses.getDestination().getName();
			if (entry.getKey().contentEquals(relationshipDescriptionProperty)) {
				relationshipDescription = entry.getValue();
			}
		}
		return relationshipDescription;
	}
	@Override
	protected String getRelationshipDescription(Container contained, Container found) {
		Relationship relationship= getCurrentRelationship(contained, found);
		String relationshipDescription = changeRelationshipDescription(relationship);
		contained.uses(found, relationshipDescription);
		return relationshipDescription;
	}
	@Override
	protected Relationship getCurrentRelationship(Container contained, Container found) {
		return contained.uses(found, null);
	}

	@Override
	protected void containedDependsUpon(Container container, Container found, String description) {
		container.uses(found, description);
	}

	@Override
	protected Collection<Container> getEnhancedChildren() {
		return enhanced.getContainers();
	}

}