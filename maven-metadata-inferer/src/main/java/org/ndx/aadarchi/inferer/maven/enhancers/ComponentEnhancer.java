package org.ndx.aadarchi.inferer.maven.enhancers;

import org.apache.maven.project.MavenProject;
import org.ndx.aadarchi.inferer.maven.MavenDetailsInfererEnhancer;
import org.ndx.aadarchi.inferer.maven.MavenPomDecorator;

import com.structurizr.model.Component;

public class ComponentEnhancer extends ModelElementMavenEnhancer<Component> {

	public ComponentEnhancer(MavenDetailsInfererEnhancer mavenDetailsInfererEnhancer, Component enhanced) {
		super(mavenDetailsInfererEnhancer, enhanced);
	}

	@Override
	protected void startEnhanceWithMavenProject(MavenProject mavenProject) {
		enhanced.setTechnology(MavenPomDecorator.decorateTechnology(mavenProject));
	}

	@Override
	protected void endEnhanceWithMavenProject(MavenProject mavenProject) {

	}
}