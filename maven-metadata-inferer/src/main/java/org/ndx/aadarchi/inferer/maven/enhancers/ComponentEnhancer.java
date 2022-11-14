package org.ndx.aadarchi.inferer.maven.enhancers;

import org.apache.maven.project.MavenProject;
import org.ndx.aadarchi.inferer.maven.MavenDetailsInfererEnhancer;

import com.structurizr.model.Component;

public class ComponentEnhancer extends ModelElementMavenEnhancer<Component> {

	/**
	 * 
	 */
	private final MavenDetailsInfererEnhancer mavenDetailsInfererEnhancer;

	public ComponentEnhancer(MavenDetailsInfererEnhancer mavenDetailsInfererEnhancer, Component enhanced) {
		super(mavenDetailsInfererEnhancer, enhanced);
		this.mavenDetailsInfererEnhancer = mavenDetailsInfererEnhancer;
	}

	@Override
	protected void startEnhanceWithMavenProject(MavenProject mavenProject) {
		enhanced.setTechnology(this.mavenDetailsInfererEnhancer.decorateTechnology(mavenProject));
	}

	@Override
	protected void endEnhanceWithMavenProject(MavenProject mavenProject) {

	}
}