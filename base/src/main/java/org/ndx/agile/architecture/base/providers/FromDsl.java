package org.ndx.agile.architecture.base.providers;

import java.io.File;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.ndx.agile.architecture.base.ArchitectureModelProvider;
import org.ndx.agile.architecture.base.enhancers.ModelElementKeys;

import com.structurizr.Workspace;
import com.structurizr.dsl.StructurizrDslParser;
import com.structurizr.dsl.StructurizrDslParserException;

@ApplicationScoped
@Named("Using workspace.dsl")
public class FromDsl implements ArchitectureModelProvider {

	private static final String WORKSPACE_DSL = ModelElementKeys.PREFIX + "dsl";
	@Inject
	@ConfigProperty(name = WORKSPACE_DSL, defaultValue = "${project.basedir}/src/architecture/resources/workspace.dsl") File workspace;

	@Override
	public Workspace describeArchitecture() {
		if(!workspace.exists()) {
			throw new UnsupportedOperationException(String.format(
					"Parsing a workspace.dsl file supposes the file exists.\n"
					+ "We tried to read file %s but there was nothing.\n"
					+ "Please either move that file into that location or set the property %s",
					workspace.getAbsolutePath(),
					WORKSPACE_DSL));
		}
		StructurizrDslParser parser = new StructurizrDslParser();
		try {
			parser.parse(workspace);
		} catch (StructurizrDslParserException e) {
			throw new RuntimeException("Can't read workspace.dsl", e);
		}
		return parser.getWorkspace();
	}

}
