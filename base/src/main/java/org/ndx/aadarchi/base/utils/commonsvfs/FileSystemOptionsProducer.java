package org.ndx.aadarchi.base.utils.commonsvfs;

import org.apache.commons.vfs2.FileSystemOptions;

import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

public class FileSystemOptionsProducer {

	@Inject Instance<FileSystemOptionsConfigurer> configurers;
	@Produces @Singleton FileSystemOptions createOptions(Instance<FileSystemOptions> updaters) {
		FileSystemOptions globalOptions = new FileSystemOptions();
		configurers.stream()
			.forEach(configurer -> configurer.accept(globalOptions));
		return globalOptions;

	}
}
