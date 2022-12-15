package org.ndx.aadarchi.base.providers;

import java.io.File;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.ndx.aadarchi.base.ArchitectureModelProvider;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys.ConfigProperties.WorkspaceDsl;

import com.structurizr.Workspace;
import com.structurizr.dsl.StructurizrDslParser;
import com.structurizr.dsl.StructurizrDslParserException;

@ApplicationScoped
@Named("Using workspace.dsl")
public class FromDsl implements ArchitectureModelProvider {

	@Inject
	@ConfigProperty(name = WorkspaceDsl.NAME, defaultValue = WorkspaceDsl.VALUE) File workspace;

	@Override
	public Workspace describeArchitecture() {
		if(!workspace.exists()) {
			throw new UnsupportedOperationException(String.format(
					"Parsing a workspace.dsl file supposes the file exists.\n"
					+ "We tried to read file %s but there was nothing.\n"
					+ "Please either move that file into that location or set the property %s",
					workspace.getAbsolutePath(),
					WorkspaceDsl.NAME));
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
