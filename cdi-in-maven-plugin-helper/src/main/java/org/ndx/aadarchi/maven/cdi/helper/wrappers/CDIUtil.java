package org.ndx.aadarchi.maven.cdi.helper.wrappers;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

import javax.inject.Qualifier;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.deltaspike.core.api.literal.DefaultLiteral;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.jboss.weld.environment.se.Weld;

/**
 * A utility class for handling CDI-specific tasks such as getting all beans of
 * a specific type or adding beans to the bean manager, ...
 *
 * @author <a href="mailto:stanley.hillner@itemis.de">Stanley Hillner</a>
 * @since 2.0.0
 */
public class CDIUtil {
	private static final String FILE_EXTENSION_CLASS = "class";

	/**
	 * @param x the object from which all qualifier annotations shall be searched
	 *          out.
	 * @return a set of all qualifiers the object's class is annotated with.
	 */
	public static Set<Annotation> getCdiQualifiers(AccessibleObject x) {
		Set<Annotation> qualifiers = new HashSet<Annotation>();
		for (Annotation annotation : x.getAnnotations()) {
			if (annotation.annotationType().isAnnotationPresent(Qualifier.class)) {
				qualifiers.add(annotation);
			}
		}
		if (qualifiers.isEmpty()) {
			qualifiers.add(new DefaultLiteral());
		}
		return qualifiers;
	}

	/**
	 * Queries the specified file container (folder or JAR file) for all class files
	 * and adds all found classes to the weld container so that these classes are
	 * later injectable.
	 *
	 * @param weld        the CDI container to add the classes to.
	 * @param classLoader the class loader used to query and load classes from the
	 *                    file container.
	 * @param container   the file container where to search classes. The container
	 *                    can be a folder or a JAR file.
	 * @param log         the log for processing output.
	 * @throws MojoExecutionException if it was not possible to query the file
	 *                                container.
	 */
	public static void addAllClasses(Weld weld, ClassLoader classLoader, File container, Log log)
			throws MojoExecutionException {
		Set<String> classNames = Collections.emptySet();
		if (container.isFile() && container.getAbsolutePath().endsWith(".jar")) {
			try {
				JarFile jarFile = new JarFile(container);
				classNames = getAllClassNames(jarFile);
			} catch (IOException e) {
				throw new MojoExecutionException(
						"Could not load the following JAR file: " + container.getAbsolutePath(), e);
			}
		} else if (container.isDirectory()) {
			classNames = getAllClassNames(container);
		}

		for (String className : classNames) {
//			try {
//				log.debug("loading class "+className);
//				Class<?> cls = classLoader.loadClass(className);
//				weld.addBeanClass(cls);
//			} catch (ClassNotFoundException e) {
//				log.error("Could not load the following class which might cause later issues: " + className);
//				if (log.isDebugEnabled()) {
//					log.debug(e);
//				}
//			}
		}
	}

	private static Set<String> getAllClassNames(JarFile f) {
		Set<String> classNames = new HashSet<String>();
		Enumeration<?> e = f.entries();
		boolean hasBeans = false;
		while (e.hasMoreElements()) {
			JarEntry je = (JarEntry) e.nextElement();
			String extension = FilenameUtils.getExtension(je.getName());
			if (FILE_EXTENSION_CLASS.equals(extension)) {
				String className = je.getName().substring(0, je.getName().lastIndexOf('.'));
				className = className.replace('/', '.');
				classNames.add(className);
			} else if("META-INF/beans.xml".equals(je.getName())) {
				hasBeans = true;
			}
		}
		if(hasBeans)
			return classNames;
		else
			return Collections.emptySet();
	}

	private static Set<String> getAllClassNames(File folder) {
		try {
			Set<String> allFiles = Files.walk(folder.toPath())
				.filter(file -> file.toFile().isFile())
				.map(file -> folder.toPath().relativize(file.toAbsolutePath()))
				.map(relativeFile -> relativeFile.toString())
				.collect(Collectors.toSet());
			if(allFiles.contains("META-INF/beans.xml")) {
				return allFiles.stream()
					.filter(relativePathName -> relativePathName.endsWith("."+FILE_EXTENSION_CLASS))
					.map(relativePathName -> relativePathName.substring(0, relativePathName.lastIndexOf('.')))
					.map(relativePathName -> relativePathName.replace('/', '.'))
					.map(relativePathName -> relativePathName.replace('\\', '.'))
					.collect(Collectors.toSet())
					;
			} else {
				return Collections.emptySet();
			}
		} catch (IOException e) {
			return Collections.emptySet();
		}
			
	}
}
