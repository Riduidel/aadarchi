package org.ndx.aadarchi.maven.plugin;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.apache.deltaspike.core.api.config.ConfigResolver.Converter;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.ndx.aadarchi.maven.cdi.helper.wrappers.AbstractCDIStarterMojo;

import com.structurizr.annotation.Component;

@Component(technology = "Java, maven")
@Mojo(name = "list-available-properties",
        defaultPhase = LifecyclePhase.PREPARE_PACKAGE,
        requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class AvailablePropertiesMojo extends AbstractCDIStarterMojo {

    public static class ListAvailableProperties implements Runnable {
        @Inject Logger logger;
        
        @Inject BeanManager manager;
        
        /**
         * Beans in these packages won't be analyzed.
         * @see #isInFilteredPackage(String)
         * @see #isNotInFilteredPackages(Bean)
         */
        Set<String> filteredPackages = new TreeSet<>(Arrays.asList(
        		"org.jboss.weld",
        		"javax.enterprise.inject",
        		"org.apache.deltaspike"));
        
        private boolean isInFilteredPackage(String packageName) {
        	for(int index=1; index<=packageName.length() && index>=0; index = packageName.indexOf('.', index+1)) {
        		String packagePrefix = packageName.substring(0, index);
        		if(filteredPackages.contains(packagePrefix)) {
        			return true;
        		}
        	}
        	return false;
        }
        
        /**
         * Test if the Bean object corresponds to a class which is not in a filtered package
         * @see #isInFilteredPackage(String)
         */
        private boolean isNotInFilteredPackages(Bean bean) {
        	return !isInFilteredPackage(bean.getBeanClass().getPackageName());
        }
        
        /**
         * Build a stream of entries where keys are injection points and values are beans
         */
        private Stream<Entry<InjectionPoint, Bean<?>>> beanToInjectionPointEntries(Bean<?> bean) {
        	return bean.getInjectionPoints().stream()
        			.map(injectionPoint -> Map.entry(injectionPoint, bean));
        }

        /**
         * Keep only injection points annotated with @ConfigProperty annotation
         */
        private boolean filterInjectionPointsHavingConfigPropertyAnnotation(Map.Entry<InjectionPoint, Bean<?>> entry) {
        	InjectionPoint injectionPoint = entry.getKey();
        	return injectionPoint.getAnnotated().getAnnotation(ConfigProperty.class)!=null;
        }
        
        private Map.Entry<ConfigProperty, Map.Entry<InjectionPoint, Bean<?>>>
        	remapToConfigPropertyKey(Map.Entry<InjectionPoint, Bean<?>> initialEntry) {
        	InjectionPoint injectionPoint = initialEntry.getKey();
        	ConfigProperty key = injectionPoint.getAnnotated().getAnnotation(ConfigProperty.class);
        	return Map.entry(key, initialEntry);
        }
        
        @Override
        public void run() {
        	// Get all the beans
        	Set<Bean<?>> allBeans = manager.getBeans(Object.class);
        	Map<String, Map<String, Map<String, Set<String>>>> configPropertiesTree = allBeans.stream()
        			.filter(this::isNotInFilteredPackages)
        			.flatMap(this::beanToInjectionPointEntries)
        			.filter(this::filterInjectionPointsHavingConfigPropertyAnnotation)
        			.map(this::remapToConfigPropertyKey)
        			/* returns Stream<Entry<ConfigProperty, Entry<InjectionPoint, Bean<?>>> */
        			.collect(Collectors.groupingBy(
        					// First level of grouping is ConfigProperty name
        					entry -> entry.getKey().name(),
        					Collectors.groupingBy(
        							// Second level of grouping is by default value
        							entry -> entry.getValue().getKey().getAnnotated().getBaseType().getTypeName(),
        							Collectors.groupingBy(entry -> entry.getKey().defaultValue(),
        									Collectors.mapping(entry -> entry.getValue().getValue().getBeanClass().getName(), Collectors.toSet()))
	        							)
	        					)
        					)
        			;
        			
        			
        	
        	// Now we have all places where Configproperty annotation is used, let's map that to our objects
        	logger.info("available config properties are\n"+configPropertiesTreeToString(configPropertiesTree));
        }

		private String configPropertiesTreeToString(Map<String, Map<String, Map<String, Set<String>>>> values) {
			return values.entrySet().stream()
					.map(entry -> String.format("* \"%s\"\n%s", entry.getKey(), 
							configPropertyValuesToString(entry.getValue())))
					.collect(Collectors.joining("\n"));
		}

		/**
		 * 
		 * @param value maps types to default values and bean classes
		 * @return
		 */
		private String configPropertyValuesToString(Map<String, Map<String, Set<String>>> value) {
			return value.entrySet().stream()
					.map(entry -> String.format("\tof type \"%s\"\n%s", entry.getKey(), defaultValuesToString(entry.getValue())))
					.collect(Collectors.joining("\n"));
		}

		private String defaultValuesToString(Map<String, Set<String>> value) {
			return value.entrySet().stream()
					.map(entry -> String.format("\t\twith default value \"%s\". Defined in types\n%s", entry.getKey(), entry.getValue().stream().map(text -> "\t\t\t"+text).collect(Collectors.joining("\n"))))
					.collect(Collectors.joining("\n"));
		}

    }

    @Override
    protected Class<? extends Runnable> getCDIEnabledRunnableClass() {
        return AvailablePropertiesMojo.ListAvailableProperties.class;
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        // Mind you, Deltaspike relies upon the presence of a context classloader to choose which
        // property loaders are available, which sometimes prevent the
        // github token to be injected
        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
        super.execute();
    }

}