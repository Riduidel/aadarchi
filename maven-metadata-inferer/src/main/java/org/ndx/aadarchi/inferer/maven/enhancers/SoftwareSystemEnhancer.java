package org.ndx.aadarchi.inferer.maven.enhancers;

import java.util.Collection;

import org.apache.maven.project.MavenProject;
import org.ndx.aadarchi.inferer.maven.MavenDetailsInfererEnhancer;

import com.structurizr.model.Container;
import com.structurizr.model.SoftwareSystem;

/**
 * Software system enhancer
 * @author nicolas-delsaux
 *
 */
public class SoftwareSystemEnhancer extends AbstractContainerEnhancer<SoftwareSystem, Container> {

	private final MavenDetailsInfererEnhancer mavenDetailsInfererEnhancer;

	public SoftwareSystemEnhancer(MavenDetailsInfererEnhancer mavenDetailsInfererEnhancer, SoftwareSystem softwareSystem) {
		super(mavenDetailsInfererEnhancer, softwareSystem);
		this.mavenDetailsInfererEnhancer = mavenDetailsInfererEnhancer;
	}

	@Override
	protected Container addContainedElementWithKey(MavenProject module, String key) {
		return enhanced.addContainer(key, module.getDescription(), this.mavenDetailsInfererEnhancer.decorateTechnology(module));
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