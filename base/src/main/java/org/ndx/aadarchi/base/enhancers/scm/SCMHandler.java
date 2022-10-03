package org.ndx.aadarchi.base.enhancers.scm;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.function.Predicate;

import javax.enterprise.inject.Instance;

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
	 * Open stream to read the file that is below the given url
	 * @param url url to read
	 * @return an input stream or an IOException if any problem occured
	 */
	InputStream openStream(URL url) throws IOException;

	/**
	 * Get all the files matching the given filename filter
	 * @param project github url of the project in which we search elements
	 * @param path the path in which we're searching for files
	 * @param filter the filter allowing us to find the files
	 * @return collection of files matching given path and predicate
	 * @throws FileNotFoundException if provider couldn't return any matching file and throwed any exception
	 * because of that
	 */
	Collection<SCMFile> find(String project, String path, Predicate<SCMFile> filter) throws FileNotFoundException;

	String linkTo(String project, String path);

	String asciidocText();

	static InputStream openStream(Instance<SCMHandler> scmHandler, URL url) throws IOException {
		if(scmHandler!=null) {
			for(SCMHandler handler : scmHandler) {
				if(handler.canHandle(url.toString())) {
					return handler.openStream(url);
				}
			}
		}
		return url.openStream();
	}

}
