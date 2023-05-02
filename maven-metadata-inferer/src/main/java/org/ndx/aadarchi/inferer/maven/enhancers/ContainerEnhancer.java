package org.ndx.aadarchi.inferer.maven.enhancers;

import java.util.Collection;
import java.util.Map;

import com.structurizr.model.Relationship;
import org.apache.maven.project.MavenProject;
import org.ndx.aadarchi.base.relationships.RelationshipsPropertiesFileReader;
import org.ndx.aadarchi.inferer.maven.MavenPomDecorator;
import org.ndx.aadarchi.inferer.maven.MavenPomReader;

import com.structurizr.model.Component;
import com.structurizr.model.Container;

public class ContainerEnhancer extends AbstractContainerEnhancer<Container, Component> {

	public ContainerEnhancer(MavenPomReader mavenPomReader, Container container) {
		super(mavenPomReader, container);
	}
	
	@Override
	protected void startEnhanceWithMavenProject(MavenProject mavenProject) {
		enhanced.setTechnology(MavenPomDecorator.decorateTechnology(mavenProject));
		super.startEnhanceWithMavenProject(mavenProject);
	}

	@Override
	protected Component addContainedElementWithKey(MavenProject module, String key) {
		return enhanced.addComponent(key, module.getDescription(), MavenPomDecorator.decorateTechnology(module));
	}

	@Override
	protected Component getContainedElementWithName(String key) {
		return enhanced.getComponentWithName(key);
	}

	@Override
	protected String getRelationshipDescription(Component contained, Component found) {
		Relationship relationship = getCurrentRelationship(contained, found);
		String relationshipDescription = changeRelationshipDescription(relationship);
		contained.uses(found, relationshipDescription);
		return relationshipDescription;
	}
	@Override
	protected String changeRelationshipDescription(Relationship uses) {
		String relationshipDescription = null;
		RelationshipsPropertiesFileReader propertiesFileReader = new RelationshipsPropertiesFileReader();
		Map<String, String> relationshipDescriptionProperties = propertiesFileReader.readPropertiesFile();
		for (Map.Entry<String, String> entry: relationshipDescriptionProperties.entrySet()) {
			String relationshipDescriptionProperty = uses.getSource().getName()+"->"+uses.getDestination().getName();
			if (entry.getKey().contentEquals(relationshipDescriptionProperty)) {
				relationshipDescription = entry.getValue();
			}
		}
		return relationshipDescription;
	}
	@Override
	protected Relationship getCurrentRelationship(Component contained, Component found) {
		return contained.uses(found, null);
	}

	@Override
	protected void containedDependsUpon(Component component, Component found, String description) {
		component.uses(found, description);
	}


	@Override
	protected Collection<Component> getEnhancedChildren() {
		return enhanced.getComponents();
	}

}