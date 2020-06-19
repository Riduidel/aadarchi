package org.ndx.agile.architecture.base;

import java.io.File;
import java.util.Comparator;
import java.util.concurrent.ForkJoinPool;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.deltaspike.core.api.config.ConfigProperty;

import com.structurizr.Workspace;
import com.structurizr.model.Component;
import com.structurizr.model.Container;
import com.structurizr.model.Element;
import com.structurizr.model.Model;
import com.structurizr.model.SoftwareSystem;
import com.structurizr.view.View;
import com.structurizr.view.ViewSet;

@ApplicationScoped
public class ArchitectureEnhancer implements OutputBuilder {
	public static final String SECTION_PATTERN = "%02d-%s";
	@Inject Instance<Enhancer> enhancers;
	@Inject Logger logger;
	@Inject @ConfigProperty(name="agile.architecture.enhancements") File enhancementsBase;

	public void enhance(Workspace workspace) {
		logger.info(() -> String.format("Enhancers applied to this architecture are\n%s",  
			enhancers.stream()
				.sorted(Comparator.comparingInt(e -> e.priority()))
				.map(e -> String.format("%s => %d", e.getClass().getName(), e.priority()))
				.collect(Collectors.joining("\n"))));
		withStopWatch("Running all enhancements took %s", () -> enhancers.stream()
			.sorted(Comparator.comparingInt(e -> e.priority()))
			.forEach(enhancer -> enhancerVisitWorkspace(enhancer, workspace)));
	}

	/**
	 * Here we abuse the Runnable interface, cause it won't be called in a separate thred !
	 * It's just the most convenient no-in no-out functionnal interface that exists
	 * @param format log message that willa ccept only one parameter: the stop watch duration
	 * @param called called runnable
	 */
	private void withStopWatch(String format, Runnable called) {
		StopWatch stopwatch = new StopWatch();
		stopwatch.start();
		try {
			called.run();
		} finally {
			stopwatch.stop();
			logger.info(String.format(format, stopwatch.toString()));
		}
	}
	
	private void enhancerVisitWorkspace(Enhancer enhancer, Workspace workspace) {
		withStopWatch(String.format("Running enhancement %s took %%s", enhancer.getClass().getName()),
				() -> {
					if(enhancer.startVisit(workspace, this)) {
						if(enhancer instanceof ModelEnhancer) {
							enhancerVisitModel((ModelEnhancer) enhancer, workspace.getModel());
						}
						if(enhancer instanceof ViewEnhancer) {
							enhancerVisitViews((ViewEnhancer) enhancer, workspace.getViews());
						}
						enhancer.endVisit(workspace, this);
					}
				}
			);
	}


	private void enhancerVisitViews(ViewEnhancer enhancer, ViewSet viewset) {
		if(enhancer.startVisit(viewset)) {
			Stream<View> views = viewset.getViews().stream();
			if(enhancer.isParallel())
				views = views.parallel();
			views.filter(s -> enhancer.startVisit(s))
				.forEach(s -> enhancer.endVisit(s, this));
			enhancer.endVisit(viewset, this);
		}
	}

	private void enhancerVisitModel(ModelEnhancer enhancer, Model model) {
		if(enhancer.startVisit(model)) {
			Stream<SoftwareSystem> systems = model.getSoftwareSystems().stream();
			if(enhancer.isParallel())
				systems = systems.parallel();
			systems.filter(s -> enhancer.startVisit(s))
				.peek(s -> enhancerVisitSystem(enhancer, s))
				.forEach(s -> enhancer.endVisit(s, this));
			enhancer.endVisit(model, this);
		}
	}


	private void enhancerVisitSystem(ModelEnhancer enhancer, SoftwareSystem system) {
		Stream<Container> containers = system.getContainers().stream();
		if(enhancer.isParallel())
			containers = containers.parallel();
		containers.filter(c -> enhancer.startVisit(c))
			.peek(c -> enhancerVisitContainer(enhancer, c))
			.forEach(c -> enhancer.endVisit(c, this));
	}


	private void enhancerVisitContainer(ModelEnhancer enhancer, Container container) {
		Stream<Component> systems = container.getComponents().stream();
		if(enhancer.isParallel())
			systems = systems.parallel();
		systems.filter(c -> enhancer.startVisit(c))
			.forEach(c -> enhancer.endVisit(c, this));
	}


	@Override
	public File outputFor(AgileArchitectureSection section, Element element, Enhancer enhancer, String format) {
		return new File(enhancementsBase,
				// Yup, we use hex values for priority, to have less characters
				String.format("%s/"+SECTION_PATTERN+"/_%08x-%s.%s", 
					element.getCanonicalName(),
					section.index(), section.name(),
					enhancer.priority(), enhancer.getClass().getSimpleName(), format
					)
				);
	}

	@Override
	public File outputFor(AgileArchitectureSection section, Element element) {
		return new File(enhancementsBase,
				// Yup, we use hex values for priority, to have less characters
				String.format("%s/"+SECTION_PATTERN, 
					element.getCanonicalName(),
					section.index(), section.name()
					)
				);
	}
}
