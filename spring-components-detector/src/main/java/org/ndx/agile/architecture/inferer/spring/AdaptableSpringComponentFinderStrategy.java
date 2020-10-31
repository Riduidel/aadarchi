package org.ndx.agile.architecture.inferer.spring;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ndx.agile.architecture.base.enhancers.ModelElementKeys;

import com.structurizr.analysis.AbstractComponentFinderStrategy;
import com.structurizr.analysis.AbstractSpringComponentFinderStrategy;
import com.structurizr.analysis.IgnoreDuplicateComponentStrategy;
import com.structurizr.analysis.SupportingTypesStrategy;
import com.structurizr.model.CodeElement;
import com.structurizr.model.CodeElementHack;
import com.structurizr.model.CodeElementRole;
import com.structurizr.model.Component;

public class AdaptableSpringComponentFinderStrategy extends AbstractSpringComponentFinderStrategy {
	private static final Logger logger = Logger.getLogger(AdaptableSpringComponentFinderStrategy.class.getName());

	public static final String SPRING_COMPONENTS_OPTIONS_FAVOR_INTERFACE = ModelElementKeys.PREFIX+"spring.options.favor.interface";

	/**
	 * A map linkin an annotation full class name to the strategy used to detect it.
	 * This exists since all annotations don't exist on Spring 3/4
	 */
	private static Map<String, String> annotationsToStrategies = Map.of(
    		"org.springframework.web.bind.annotation.RestController","com.structurizr.analysis.SpringRestControllerComponentFinderStrategy",
    		"org.springframework.stereotype.Controller", "com.structurizr.analysis.SpringMvcControllerComponentFinderStrategy",
    		"org.springframework.stereotype.Service", "com.structurizr.analysis.SpringServiceComponentFinderStrategy",
    		"org.springframework.stereotype.Component", "com.structurizr.analysis.SpringComponentComponentFinderStrategy",
    		"org.springframework.stereotype.Repository", "org.ndx.agile.architecture.inferer.spring.SpringRepositoryComponentFinderStrategy",
    		"com.sun.jmx.mbeanserver.Repository", "com.structurizr.analysis.SpringRepositoryComponentFinderStrategy"
			);
	
	private static Collection<Class<? extends AbstractSpringComponentFinderStrategy>> strategies = null;

	private static Collection<Class<? extends AbstractSpringComponentFinderStrategy>> getStrategies() {
		if(strategies==null) {
			synchronized(AdaptableSpringComponentFinderStrategy.class) {
				if(strategies==null) {
					ClassLoader classLoader = AdaptableSpringComponentFinderStrategy.class.getClassLoader();
					Collection<Class<? extends AbstractSpringComponentFinderStrategy>> newStrategies = new LinkedList<Class<? extends AbstractSpringComponentFinderStrategy>>();
			        for(Map.Entry<String, String> annotationToStrategy : annotationsToStrategies.entrySet()) {
			        	try {
			        		// We first load the key to see if it is accessible
			        		classLoader.loadClass(annotationToStrategy.getKey());
							Class<? extends AbstractSpringComponentFinderStrategy> strategyClass = (Class<? extends AbstractSpringComponentFinderStrategy>) classLoader.loadClass(annotationToStrategy.getValue());
							newStrategies.add(strategyClass);
			        	} catch(Exception e) {
							logger.log(Level.WARNING, 
									String.format("Unable to load annotation %s assocaited strategy %s. Maybe your Spring version is not compatible ...", annotationToStrategy.getKey(), annotationToStrategy.getValue()),
									e);
			        	}
			        }
			        strategies = newStrategies;
				}
			}
		}
		return strategies;
	}
	
	private List<AbstractSpringComponentFinderStrategy> componentFinderStrategies = new LinkedList<>();

    public AdaptableSpringComponentFinderStrategy(SupportingTypesStrategy... strategies) {
        super(strategies);
        setDuplicateComponentStrategy(new IgnoreDuplicateComponentStrategy());
    }
    
    @Override
    public void beforeFindComponents() {
        super.beforeFindComponents();
        
        Class<SupportingTypesStrategy[]> parameters = SupportingTypesStrategy[].class;
        Object[] supportingStrategiesValues = new Object[] {
        		supportingTypesStrategies.toArray(new SupportingTypesStrategy[supportingTypesStrategies.size()])
        };

        for(Class<? extends AbstractSpringComponentFinderStrategy> strategyClass : getStrategies()) {
        	try {
        		Constructor<?> constructor = strategyClass.getDeclaredConstructor(parameters);
				AbstractSpringComponentFinderStrategy newInstance = (AbstractSpringComponentFinderStrategy) constructor.newInstance(supportingStrategiesValues);
				componentFinderStrategies.add(newInstance);
			} catch (Exception e) {
				logger.log(Level.WARNING, 
						String.format("Unable to create instance ofs %s. Maybe your Spring version is not compatible ...", strategyClass.getCanonicalName()),
						e);
			}
        }

        for (AbstractSpringComponentFinderStrategy componentFinderStrategy : componentFinderStrategies) {
            componentFinderStrategy.setIncludePublicTypesOnly(includePublicTypesOnly);
            componentFinderStrategy.setComponentFinder(getComponentFinder());
            supportingTypesStrategies.forEach(componentFinderStrategy::addSupportingTypesStrategy);
            componentFinderStrategy.setDuplicateComponentStrategy(getDuplicateComponentStrategy());
            componentFinderStrategy.beforeFindComponents();
        }
    }

    @Override
    protected Set<Component> doFindComponents() {
        Set<Component> components = new HashSet<>();

        for (AbstractComponentFinderStrategy componentFinderStrategy : componentFinderStrategies) {
            components.addAll(componentFinderStrategy.findComponents());
        }

        return components;
    }
    
    /**
     * In some Spring projects, recognized components are the implementations.
     * In such a case, to ease dependency detection, it's a good thing to replace implementation by interfaces.
     * As it has to be done after supporting type detection, but before dependency resolution,
     * we have to rewrite the whole {@link AbstractSpringComponentFinderStrategy#afterFindComponents()} method
     */
    @Override
    public void afterFindComponents() {
    	findSupportingTypes(componentsFound);
    	if(componentFinder.getContainer().getProperties().containsKey(SPRING_COMPONENTS_OPTIONS_FAVOR_INTERFACE)) {
    		logger.warning(String.format("Container %s requires its components to favor interfaces. So we're replacing all impls by interfaces", componentFinder.getContainer().getName()));
    		for(Component component : componentsFound) {
    			String componentBaseName = component.getName();
    			componentBaseName = componentBaseName.replace("Impl", "");
    			for(CodeElement element : component.getCode()) {
    				if(element.getName().contains(componentBaseName)) {
    					switch(element.getCategory()) {
    					case "interface":
    						CodeElementHack.setRole(element, CodeElementRole.Primary);
    						break;
    					case "class":
    						CodeElementHack.setRole(element, CodeElementRole.Supporting);
    						break;
    					}
    				}
    			}
    		}
    	}
        findDependencies();
    }
}
