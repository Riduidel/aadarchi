package org.ndx.aadarchi.base.enhancers.scm;

import java.io.InputStream;

public interface SCMFile {
	String name();

	InputStream content();

	long lastModified();

}
