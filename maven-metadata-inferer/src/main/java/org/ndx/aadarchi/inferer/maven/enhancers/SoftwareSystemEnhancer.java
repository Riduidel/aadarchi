package org.ndx.aadarchi.inferer.maven.enhancers;

import java.util.Collection;

import org.apache.maven.project.MavenProject;
import org.ndx.aadarchi.base.utils.descriptions.RelationshipDescriptionProvider;
import org.ndx.aadarchi.inferer.maven.MavenPomDecorator;
import org.ndx.aadarchi.inferer.maven.MavenPomReader;

import com.structurizr.Workspace;
import com.structurizr.model.Container;
import com.structurizr.model.SoftwareSystem;

public class SoftwareSystemEnhancer extends AbstractContainerEnhancer<SoftwareSystem, Container> {

	public SoftwareSystemEnhancer(MavenPomReader mavenPomReader, Workspace workspace, SoftwareSystem softwareSystem, RelationshipDescriptionProvider descriptionProvider) {
		super(mavenPomReader, workspace, softwareSystem, descriptionProvider);
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
	protected void containedDependsUpon(Container contained, Container found, String string) {
		contained.uses(found, string);
	}

	@Override
	protected Collection<Container> getEnhancedChildren() {
		return enhanced.getContainers();
	}

}