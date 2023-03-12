package org.ndx.aadarchi.inferer.maven;

import java.util.Set;
import java.util.Stack;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import org.ndx.aadarchi.base.ModelEnhancer;
import org.ndx.aadarchi.base.OutputBuilder;
import org.ndx.aadarchi.base.enhancers.ModelElementAdapter;
import org.ndx.aadarchi.inferer.maven.enhancers.ComponentEnhancer;
import org.ndx.aadarchi.inferer.maven.enhancers.ContainerEnhancer;
import org.ndx.aadarchi.inferer.maven.enhancers.SoftwareSystemEnhancer;

import com.structurizr.model.Component;
import com.structurizr.model.Container;
import com.structurizr.model.SoftwareSystem;
import com.structurizr.model.StaticStructureElement;

/**
 * An enhancer trying to read as much informations as possible from maven pom.
 * 
 * @author nicolas-delsaux
 *
 */
@Default
@ApplicationScoped
@com.structurizr.annotation.Component
public class MavenDetailsInfererEnhancer extends ModelElementAdapter implements ModelEnhancer {
	@Inject protected Logger logger;

	@Inject MavenPomReader mavenPomReader;

	Stack<Set<? extends StaticStructureElement>> stack = new Stack<>();

	@Override
	public boolean isParallel() {
		return true;
	}

	/**
	 * @return priority is set to one to have this enhancer run almost first
	 */
	@Override
	public int priority() {
		return 1;
	}

	@Override
	public boolean startVisit(SoftwareSystem softwareSystem) {
		stack.push(softwareSystem.getContainers());
		int containersNumber = stack.get(0).size();
		logger.info(String.format("Starting visit of system %s, there are %d containers", softwareSystem, containersNumber));
		logger.fine(String.format("Starting visit of system %s, , there are these containers : %s ", softwareSystem, softwareSystem.getContainers()));
		new SoftwareSystemEnhancer(mavenPomReader, softwareSystem).startEnhance(mavenPomReader::processModelElement);
		return super.startVisit(softwareSystem);
	}

	@Override
	public void endVisit(SoftwareSystem softwareSystem, OutputBuilder builder) {
		Set<? extends StaticStructureElement> initial = stack.pop();
		int initialContainersNumber = initial.size();
		int actualContainersNumber = softwareSystem.getContainers().size();
		int newContainersNumber = actualContainersNumber - initialContainersNumber;
		Set<? extends StaticStructureElement> newContainers = softwareSystem.getContainers().stream().filter(element -> !initial.contains(element))
				.collect(Collectors.toSet());
		if( actualContainersNumber > initialContainersNumber) {
			logger.info(String.format("Ending visit of system %s, there are %d containers, including %d new containers", softwareSystem, actualContainersNumber, newContainersNumber));
			logger.fine(String.format("Ending visit of system %s, there are these new containers : %s", softwareSystem, newContainers ));
		} else
			logger.info("At the end, there are no new containers.");
		new SoftwareSystemEnhancer(mavenPomReader, softwareSystem).endEnhance(mavenPomReader::processModelElement);
	}

	@Override
	public boolean startVisit(Container container) {
		new ContainerEnhancer(mavenPomReader, container).startEnhance(mavenPomReader::processModelElement);
		return super.startVisit(container);
	}

	@Override
	public void endVisit(Container container, OutputBuilder builder) {
		new ContainerEnhancer(mavenPomReader, container).endEnhance(mavenPomReader::processModelElement);
	}

	@Override
	public boolean startVisit(Component component) {
		new ComponentEnhancer(mavenPomReader, component).startEnhance(mavenPomReader::processModelElement);
		return super.startVisit(component);
	}

	@Override
	public void endVisit(Component component, OutputBuilder builder) {
		new ComponentEnhancer(mavenPomReader, component).endEnhance(mavenPomReader::processModelElement);
	}
}