package org.ndx.aadarchi.maven.plugin;

import java.util.Arrays;
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
        public static class ConfigPropertyLiteral extends AnnotationLiteral<ConfigProperty> implements ConfigProperty {

            private String name;
            private String defaultValue;
            private boolean projectStageAware;
            private String parameterizedBy;
            private boolean evaluateVariables;
            private Class<? extends Converter> converter;
            private long cacheFor;
            private TimeUnit cacheUnit;

            public ConfigPropertyLiteral() {
                super();
                this.name = ConfigProperty.NULL;
            }

            public ConfigPropertyLiteral name(String name) {
                this.name = name;
                return this;
            }

            @Override
            public String name() {
                return name;
            }

            public ConfigPropertyLiteral defaultValue(String defaultValue) {
                this.defaultValue = defaultValue;
                return this;
            }

            @Override
            public String defaultValue() {
                return defaultValue;
            }

            public ConfigPropertyLiteral projectStageAware(boolean projectStageAware) {
                this.projectStageAware = projectStageAware;
                return this;
            }

            @Override
            public boolean projectStageAware() {
                return projectStageAware;
            }

            public ConfigPropertyLiteral parameterizedBy(String parameterizedBy) {
                this.parameterizedBy = parameterizedBy;
                return this;
            }

            @Override
            public String parameterizedBy() {
                return parameterizedBy;
            }

            public ConfigPropertyLiteral evaluateVariables(boolean evaluateVariables) {
                this.evaluateVariables = evaluateVariables;
                return this;
            }

            @Override
            public boolean evaluateVariables() {
                return evaluateVariables;
            }

            public ConfigPropertyLiteral converter(Class<? extends Converter> converter) {
                this.converter = converter;
                return this;
            }

            @Override
            public Class<? extends Converter> converter() {
                return converter;
            }

            public ConfigPropertyLiteral cacheFor(long cacheFor) {
                this.cacheFor = cacheFor;
                return this;
            }

            @Override
            public long cacheFor() {
                return cacheFor;
            }

            public ConfigPropertyLiteral cacheUnit(TimeUnit cacheUnit) {
                this.cacheUnit = cacheUnit;
                return this;
            }

            @Override
            public TimeUnit cacheUnit() {
                return cacheUnit;
            }

        }
        
        @Inject Logger logger;
        
        @Inject BeanManager manager;
        
        Set<String> filteredPackages = new TreeSet<>(Arrays.asList("org.apache.deltaspike"));
        
        private boolean isInFilteredPackage(String packageName) {
        	for(int index=1; index<=packageName.length() && index>=0; index = packageName.indexOf('.', index+1)) {
        		String packagePrefix = packageName.substring(0, index);
        		if(filteredPackages.contains(packagePrefix)) {
        			return true;
        		}
        	}
        	return false;
        }
        
        private boolean isNotInFilteredPackages(Bean bean) {
        	return !isInFilteredPackage(bean.getBeanClass().getPackageName());
        }
        
        private Stream<Entry<InjectionPoint, Bean>> beanToInjectionPointEntries(Bean<?> bean) {
        	return bean.getInjectionPoints().stream()
        			.map(injectionPoint -> Map.entry(injectionPoint, bean));
        }
        
        private boolean filterInjectionPoints(Map.Entry<InjectionPoint, Bean> entry) {
        	InjectionPoint injectionPoint = entry.getKey();
        	return injectionPoint.getAnnotated().getAnnotation(ConfigProperty.class)!=null;
        }
        
        private String injectionPointEntryToString(Map.Entry<InjectionPoint, Bean<?>> entry) {
        	InjectionPoint injectionPoint = entry.getKey();
			ConfigProperty configProperty = injectionPoint.getAnnotated().getAnnotation(ConfigProperty.class);
        	return String.format("%s of type %s (default value: \"%s\", used in %s)", 
        			configProperty.name(), 
        			injectionPoint.getAnnotated().getBaseType().getTypeName(), 
        			configProperty.defaultValue(), 
        			entry.getValue().getBeanClass().getName());
        }
        
        @Override
        public void run() {
        	// Get all the beans
        	Set<Bean<?>> allBeans = manager.getBeans(Object.class);
        	Map<InjectionPoint, Bean<?>> configPropertiesInjectionPoints = allBeans.stream()
        			.filter(this::isNotInFilteredPackages)
        			.flatMap(this::beanToInjectionPointEntries)
        			.filter(this::filterInjectionPoints)
        			.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
        	
        	String text = configPropertiesInjectionPoints.entrySet().stream()
        			.map(this::injectionPointEntryToString)
        			.sorted()
        			.collect(Collectors.joining("\n"));
        	// Now we have all places where Configproperty annotation is used, let's map that to our objects
        	logger.info(String.format("%d config properties loaded\n%s", configPropertiesInjectionPoints.size(), text));
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