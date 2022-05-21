package org.ndx.agile.architecture.documentation.system.maven.plugin.cdi;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.impl.ArtifactResolver;
import org.jboss.weld.config.ConfigurationKey;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.ndx.agile.architecture.documentation.system.maven.plugin.BindJULToMaven;

/**
 * A base class bootstraping Weld in maven context and ensuring all Maven
 * components are correctly injected in CDI scope
 * 
 * @author nicolas-delsaux
 *
 */
public abstract class AbstractCDIStarterMojo extends AbstractMojo implements Extension {

	@Component
	@MojoProduces
	private ArtifactResolver resolver;

	@Component
	@MojoProduces
	private MavenProject project;

	@Component
	@MojoProduces
	private MavenSession session;

	@Component
	@MojoProduces
	private MojoExecution mojoExecution;

	@Component
	private PluginDescriptor pluginDescriptor;

	@Parameter(readonly = true, defaultValue = "${repositorySystemSession}")
	private RepositorySystemSession repositorySystemSession;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		BindJULToMaven.accept(getLog());
		System.setProperty(ConfigurationKey.EXECUTOR_THREAD_POOL_TYPE.get(), "NONE");
		Weld containerInitializer = new Weld();
		containerInitializer.addExtensions(this);
		addDependenciesFrom(containerInitializer, pluginDescriptor.getPlugin().getDependencies());
		addArtifactsFrom(containerInitializer, project.getCompileArtifacts());
		File outputClasses = new File(project.getBuild().getOutputDirectory());
		if (outputClasses.exists()) {
			ClassRealm realm = pluginDescriptor.getClassRealm();
			try {
				realm.addURL(outputClasses.toURI().toURL());
				CDIUtil.addAllClasses(containerInitializer, getClass().getClassLoader(), outputClasses, getLog());
			} catch (MalformedURLException e) {
				throw new MojoExecutionException("Unable to load output folder as classloader library", e);
			}
		}
		// Don't forget to add local classes if they exist
		WeldContainer container = (WeldContainer) containerInitializer.initialize();
		try {
			Runnable cdiEnabledMojo = getCDIEnabledRunnable(container);
			cdiEnabledMojo.run();
		} finally {
			container.close();
		}
	}

	@SuppressWarnings("unused")
	// will be called automatically by the CDI container once the bean discovery has
	// finished
	private void processMojoCdiProducerFields(@Observes AfterBeanDiscovery event, BeanManager beanManager)
			throws MojoExecutionException {

		Class<?> cls = getClass();
		Set<Field> fields = new HashSet<Field>();

		while (cls != AbstractMojo.class) {
			fields.addAll(Arrays.asList(cls.getFields()));
			fields.addAll(Arrays.asList(cls.getDeclaredFields()));
			cls = cls.getSuperclass();
		}

		for (Field f : fields) {
			if (f.isAnnotationPresent(MojoProduces.class)) {
				try {
					f.setAccessible(true);
					event.addBean(new CdiBeanWrapper<Object>(f.get(this), f.getGenericType(), f.getType(),
							CDIUtil.getCdiQualifiers(f)));
				} catch (Throwable t) {
					throw new MojoExecutionException("Could not process CDI producer field of the Mojo.", t);
				}
			}
		}
	}

	@SuppressWarnings({ "unused", "unchecked", "rawtypes" })
	// will be called automatically by the CDI container once the bean discovery has
	// finished
	private void processMojoCdiProducerMethods(@Observes AfterBeanDiscovery event, BeanManager beanManager)
			throws MojoExecutionException {
		// no method parameter injection possible at the moment since the container is
		// not yet initialized at this point!
		Class<?> cls = getClass();
		Set<Method> methods = new HashSet<Method>();

		while (cls != AbstractMojo.class) {
			methods.addAll(Arrays.asList(cls.getMethods()));
			methods.addAll(Arrays.asList(cls.getDeclaredMethods()));
			cls = cls.getSuperclass();
		}

		for (Method m : methods) {
			if (m.getReturnType() != Void.class && m.isAnnotationPresent(MojoProduces.class)) {
				try {
					event.addBean(new CdiProducerBean(m, this, beanManager, m.getGenericReturnType(), m.getReturnType(),
							CDIUtil.getCdiQualifiers(m)));
				} catch (Throwable t) {
					throw new MojoExecutionException("Could not process CDI producer method of the Mojo.", t);
				}
			}
		}
	}

	private void addDependenciesFrom(Weld weld, List<Dependency> dependencies) throws MojoExecutionException {
		for (Dependency d : dependencies) {
			Optional<File> f = MavenUtil.resolveDependency(d, project.getRemotePluginRepositories(), this.resolver,
					this.repositorySystemSession);
			if (f.isPresent()) {
				File file = f.get();
				ClassRealm realm = pluginDescriptor.getClassRealm();
				try {
					realm.addURL(file.toURI().toURL());
				} catch (MalformedURLException e) {
					throw new MojoExecutionException("Unable to parse file path as url", e);
				}
				CDIUtil.addAllClasses(weld, getClass().getClassLoader(), file, getLog());
			} else {
				throw new MojoExecutionException("Could not resolve the following plugin dependency: " + d);
			}
		}
	}

	private void addArtifactsFrom(Weld weld, List<Artifact> dependencies) throws MojoExecutionException {
		for (Artifact a : dependencies) {
			File file = a.getFile();
			ClassRealm realm = pluginDescriptor.getClassRealm();
			try {
				realm.addURL(file.toURI().toURL());
			} catch (MalformedURLException e) {
				throw new MojoExecutionException("Unable to parse file path as url", e);
			}
		}
		// Here we perform operations in two phases in order to have jars loaded BEFORE trying to load beans in these classes
		for (Artifact a : dependencies) {
			getLog().debug("loading classes from "+a);
			CDIUtil.addAllClasses(weld, getClass().getClassLoader(), a.getFile(), getLog());
		}
	}

	protected Runnable getCDIEnabledRunnable(SeContainer container) {
		return container.select(getCDIEnabledRunnableClass()).get();
	}

	protected abstract Class<? extends Runnable> getCDIEnabledRunnableClass();
}
