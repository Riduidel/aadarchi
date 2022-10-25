package org.ndx.aadarchi.base;

import java.io.File;
import java.util.Comparator;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import com.structurizr.view.FilteredView;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys.ConfigProperties.EnhancementsDir;
import org.ndx.aadarchi.base.utils.SimpleOutputBuilder;

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
@com.structurizr.annotation.Component(technology = "Java, CDI")
@ApplicationScoped
public class ArchitectureEnhancer {
	@Inject @UsesComponent(description="Uses all enhancers") Instance<Enhancer> enhancers;
	@Inject Logger logger;
	@Inject @ConfigProperty(name=EnhancementsDir.NAME, defaultValue = EnhancementsDir.VALUE) File enhancementsBase;

	private OutputBuilder outputBuilder;
	/**
	 * We memorize the used context classloader in order for parallel streams to be able to use it.
	 */
	private ClassLoader classloader;

	/**
	 * Force the context class loader of current thread to a given one prior to running the given runnable
	 * @param runnable
	 */
	private void withClassLoader(Runnable runnable) {
		Thread.currentThread().setContextClassLoader(classloader);
		runnable.run();
	}

	/**
	 * Force the context class loader of current thread to a given one prior to running the given supplier
	 * @param runnable
	 */
	private <Type> Type withClassLoader(Supplier<Type> supplier) {
		Thread.currentThread().setContextClassLoader(classloader);
		return supplier.get();
	}
	
	@PostConstruct public void loadOutputBuilder() {
		outputBuilder = new SimpleOutputBuilder(enhancementsBase);
	}

	public void enhance(Workspace workspace) {
		classloader = Thread.currentThread().getContextClassLoader();
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
		} catch(RuntimeException e) {
			logger.log(Level.SEVERE, "Something wrong happened", e);
			throw e;
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
			views.filter(s -> withClassLoader(() -> enhancer.startVisit(s)))
				.forEach(s -> withClassLoader(() -> enhancer.endVisit(s, outputBuilder)));
			enhancer.endVisit(viewset, outputBuilder);

			Stream<FilteredView> filteredViews = viewset.getFilteredViews().stream();
			if (enhancer.isParallel())
				filteredViews = filteredViews.parallel();
			filteredViews.filter(s -> withClassLoader(() -> enhancer.startVisit(s)))
					.forEach(s -> withClassLoader(() -> enhancer.endVisit(s, outputBuilder)));
			enhancer.endVisit(viewset, outputBuilder);
		}
	}

	private void enhancerVisitModel(ModelEnhancer enhancer, Model model) {
		if(enhancer.startVisit(model)) {
			Stream<SoftwareSystem> systems = model.getSoftwareSystems().stream();
			if(enhancer.isParallel())
				systems = systems.parallel();
			systems.filter(s -> withClassLoader(() -> enhancer.startVisit(s)))
				.peek(s -> withClassLoader(() -> enhancerVisitSystem(enhancer, s)))
				.forEach(s -> withClassLoader(() -> enhancer.endVisit(s, outputBuilder)));
			enhancer.endVisit(model, outputBuilder);
		}
	}


	private void enhancerVisitSystem(ModelEnhancer enhancer, SoftwareSystem system) {
		Stream<Container> containers = system.getContainers().stream();
		if(enhancer.isParallel())
			containers = containers.parallel();
		containers.filter(c -> withClassLoader(() -> enhancer.startVisit(c)))
			.peek(c -> withClassLoader(() -> enhancerVisitContainer(enhancer, c)))
			.forEach(c -> withClassLoader(() -> enhancer.endVisit(c, outputBuilder)));
	}


	private void enhancerVisitContainer(ModelEnhancer enhancer, Container container) {
		Stream<Component> systems = container.getComponents().stream();
		if(enhancer.isParallel())
			systems = systems.parallel();
		systems.filter(c -> withClassLoader(() -> enhancer.startVisit(c)))
			.forEach(c -> withClassLoader(() -> enhancer.endVisit(c, outputBuilder)));
	}

}
