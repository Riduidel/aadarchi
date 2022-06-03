package org.ndx.agile.architecture.documentation.system.maven.plugin;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.ndx.agile.architecture.documentation.system.maven.cdi.helper.wrappers.AbstractCDIStarterMojo;

@Mojo(name = "generate-model", 
	defaultPhase = LifecyclePhase.PREPARE_PACKAGE,
	requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class GenerateDiagramsMojo extends AbstractCDIStarterMojo {

	@Override
	protected Class<? extends Runnable> getCDIEnabledRunnableClass() {
		return GenerateDiagramsRunnable.class;
	}

}