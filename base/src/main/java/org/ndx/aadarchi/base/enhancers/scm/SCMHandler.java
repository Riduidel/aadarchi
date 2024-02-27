package org.ndx.aadarchi.base.enhancers.scm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.function.Predicate;

import jakarta.enterprise.inject.Instance;

import org.apache.commons.vfs2.FileObject;

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
	 * Access to project root as stored remotely.
	 * This method won't fetch project localy but rather access it directly from server location,
	 * so take care to access time and API limits
	 * @param project project to access root of. Branch is yet chosen "randomly"
	 * @return a FileObject linking to project root.
	 */
	FileObject getProjectRoot(String project);

	String linkTo(String project, String path);

	String asciidocText();

	/**
	 * Checkout given SCM project into given checkout location
	 * @param project project to checkout
	 * @param checkoutLocation location where to checkout project
	 * @throws IOException if checkout fails
	 */
	void checkout(String projectUrl, File checkoutLocation) throws IOException;

}
