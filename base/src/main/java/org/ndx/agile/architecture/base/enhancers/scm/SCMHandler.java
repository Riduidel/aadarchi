package org.ndx.agile.architecture.base.enhancers.scm;

import java.util.Collection;
import java.util.function.Predicate;

/**
 * Interface implemented by SCM handlers to read files from SCM storage
 * @author nicolas-delsaux
 *
 */
public interface SCMHandler {
	/**
	 * 
	 * @param project
	 * @return true if this scm handler can handle the given project url
	 */
	boolean canHandle(String project);

	/**
	 * Get all the files matching the given filename filter
	 * @param project
	 * @param path
	 * @param filter
	 * @return
	 */
	Collection<SCMFile> find(String project, String path, Predicate<SCMFile> filter);

	String linkTo(String project, String path);

	String asciidocText();

}
