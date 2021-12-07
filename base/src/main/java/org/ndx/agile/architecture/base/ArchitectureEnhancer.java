package org.ndx.agile.architecture.base;

import java.io.File;
import java.util.Comparator;
import java.util.ServiceLoader;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.configuration2.ImmutableConfiguration;
import org.apache.commons.lang3.time.StopWatch;
import org.ndx.agile.architecture.base.enhancers.ModelElementKeys;
import org.ndx.agile.architecture.base.utils.SimpleOutputBuilder;

import com.structurizr.Workspace;
import com.structurizr.annotation.UsesComponent;
import com.structurizr.model.Component;
import com.structurizr.model.Container;
import com.structurizr.model.Model;
import com.structurizr.model.SoftwareSystem;
import com.structurizr.view.View;
import com.structurizr.view.ViewSet;

/**
 * Invokes all {@link ModelEnhancer} and {@link ViewEnhancer} on the produced architecture description.
 * @author nicolas-delsaux
 *
 */
@com.structurizr.annotation.Component(technology = "Java/CDI")
public class ArchitectureEnhancer {
	@UsesComponent(description="Uses all enhancers") ServiceLoader<Enhancer> enhancers;
	private static final Logger logger = Logger.getLogger(ArchitectureEnhancer.class.getName());
	File enhancementsBase;

	private OutputBuilder outputBuilder;
	private ImmutableConfiguration configuration;
	
	public ArchitectureEnhancer(ImmutableConfiguration configuration) {
		this.configuration = configuration;
		enhancementsBase = configuration.get(File.class, ModelElementKeys.PREFIX+"enhancements");
		enhancers = ServiceLoader.load(Enhancer.class);
		outputBuilder = new SimpleOutputBuilder(enhancementsBase);
	}

	public void enhance(Workspace workspace) {
		logger.info(String.format("Enhancers applied to this architecture are\n%s", 
			StreamSupport.stream(enhancers.spliterator(), false)
				.peek(enhancer -> enhancer.configure(configuration))
				.sorted(Comparator.comparingInt(e -> e.priority()))
				.map(e -> String.format("%s => %d", e.getClass().getName(), e.priority()))
				.collect(Collectors.joining("\n"))));
		withStopWatch("Running all enhancements took %s", () -> StreamSupport.stream(enhancers.spliterator(), false)
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
					if(enhancer.startVisit(workspace, outputBuilder)) {
						if(enhancer instanceof ModelEnhancer) {
							enhancerVisitModel((ModelEnhancer) enhancer, workspace.getModel());
						}
						if(enhancer instanceof ViewEnhancer) {
							enhancerVisitViews((ViewEnhancer) enhancer, workspace.getViews());
						}
						enhancer.endVisit(workspace, outputBuilder);
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
				.forEach(s -> enhancer.endVisit(s, outputBuilder));
			enhancer.endVisit(viewset, outputBuilder);
		}
	}

	private void enhancerVisitModel(ModelEnhancer enhancer, Model model) {
		if(enhancer.startVisit(model)) {
			Stream<SoftwareSystem> systems = model.getSoftwareSystems().stream();
			if(enhancer.isParallel())
				systems = systems.parallel();
			systems.filter(s -> enhancer.startVisit(s))
				.peek(s -> enhancerVisitSystem(enhancer, s))
				.forEach(s -> enhancer.endVisit(s, outputBuilder));
			enhancer.endVisit(model, outputBuilder);
		}
	}


	private void enhancerVisitSystem(ModelEnhancer enhancer, SoftwareSystem system) {
		Stream<Container> containers = system.getContainers().stream();
		if(enhancer.isParallel())
			containers = containers.parallel();
		containers.filter(c -> enhancer.startVisit(c))
			.peek(c -> enhancerVisitContainer(enhancer, c))
			.forEach(c -> enhancer.endVisit(c, outputBuilder));
	}


	private void enhancerVisitContainer(ModelEnhancer enhancer, Container container) {
		Stream<Component> systems = container.getComponents().stream();
		if(enhancer.isParallel())
			systems = systems.parallel();
		systems.filter(c -> enhancer.startVisit(c))
			.forEach(c -> enhancer.endVisit(c, outputBuilder));
	}

}
