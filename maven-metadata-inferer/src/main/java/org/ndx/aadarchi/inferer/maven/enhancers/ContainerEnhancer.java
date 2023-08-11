package org.ndx.aadarchi.inferer.maven.enhancers;

import java.util.Collection;

import org.apache.maven.project.MavenProject;
import org.ndx.aadarchi.base.utils.descriptions.RelationshipDescriptionProvider;
import org.ndx.aadarchi.inferer.maven.MavenPomDecorator;
import org.ndx.aadarchi.inferer.maven.MavenPomReader;

import com.structurizr.Workspace;
import com.structurizr.model.Component;
import com.structurizr.model.Container;

public class ContainerEnhancer extends AbstractContainerEnhancer<Container, Component> {

	public ContainerEnhancer(MavenPomReader mavenPomReader, Workspace workspace, Container container, RelationshipDescriptionProvider descriptionProvider) {
		super(mavenPomReader, workspace, container, descriptionProvider);
	}
	
	@Override
	protected void startEnhanceWithMavenProject(MavenProject mavenProject) {
		super.startEnhanceWithMavenProject(mavenProject);
	}

	@Override
	protected Component addContainedElementWithKey(MavenProject module, String key) {
		return enhanced.addComponent(key, module.getDescription());
	}

	@Override
	protected Component getContainedElementWithName(String key) {
		return enhanced.getComponentWithName(key);
	}

	@Override
	protected void containedDependsUpon(Component contained, Component found, String string) {
		contained.uses(found, string);
	}

	@Override
	protected Collection<Component> getEnhancedChildren() {
		return enhanced.getComponents();
	}

}