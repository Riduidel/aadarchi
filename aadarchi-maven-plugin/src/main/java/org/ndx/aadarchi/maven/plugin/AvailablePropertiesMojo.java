package org.ndx.aadarchi.maven.plugin;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.enterprise.inject.Instance;
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

        @Inject
        Logger logger;

        /**
         * Here we inject the most generic possible CDI instance object
         * Because we will use programmatic injection later on
         * @see somewhere in https://docs.jboss.org/weld/reference/latest/en-US/html_single/
         */
        @Inject
        public Instance<Object> configPropeties;

        @Override
        public void run() {
            Instance<?> injected = configPropeties.select(new ConfigPropertyLiteral());
            // TODO convert to InjectionPoint
            // TODO output list
            logger.info("We're in");
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