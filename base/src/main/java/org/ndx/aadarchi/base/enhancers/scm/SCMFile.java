package org.ndx.aadarchi.base.enhancers.scm;

import java.io.InputStream;
import java.net.URL;

public interface SCMFile {
	String name();

	InputStream content();

	long lastModified();

	String url();

}
