package org.ndx.aadarchi.inferer.maven.enhancers;

import java.util.Collection;

import org.apache.maven.project.MavenProject;
import org.ndx.aadarchi.inferer.maven.MavenDetailsInfererEnhancer;

import com.structurizr.model.Component;
import com.structurizr.model.Container;

public class ContainerEnhancer extends AbstractContainerEnhancer<Container, Component> {

	/**
	 * 
	 */
	private final MavenDetailsInfererEnhancer mavenDetailsInfererEnhancer;

	public ContainerEnhancer(MavenDetailsInfererEnhancer mavenDetailsInfererEnhancer, Container container) {
		super(mavenDetailsInfererEnhancer, container);
		this.mavenDetailsInfererEnhancer = mavenDetailsInfererEnhancer;
	}
	
	@Override
	protected void startEnhanceWithMavenProject(MavenProject mavenProject) {
		enhanced.setTechnology(this.mavenDetailsInfererEnhancer.decorateTechnology(mavenProject));
		super.startEnhanceWithMavenProject(mavenProject);
	}

	@Override
	protected Component addContainedElementWithKey(MavenProject module, String key) {
		return enhanced.addComponent(key, module.getDescription(), this.mavenDetailsInfererEnhancer.decorateTechnology(module));
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