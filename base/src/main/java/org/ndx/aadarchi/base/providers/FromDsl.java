package org.ndx.aadarchi.base.providers;

import java.io.File;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.ndx.aadarchi.base.ArchitectureModelProvider;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys.ConfigProperties.WorkspaceDsl;
import org.ndx.aadarchi.base.utils.CantAccessPath;

import com.structurizr.Workspace;
import com.structurizr.dsl.StructurizrDslParser;
import com.structurizr.dsl.StructurizrDslParserException;

@ApplicationScoped
@Named("Using workspace.dsl")
public class FromDsl implements ArchitectureModelProvider {

	@Inject
	@ConfigProperty(name = WorkspaceDsl.NAME, defaultValue = WorkspaceDsl.VALUE) FileObject workspace;

	@Override
	public Workspace describeArchitecture() {
		try {
			if(!workspace.exists()) {
				throw new UnsupportedOperationException(String.format(
						"Parsing a workspace.dsl file supposes the file exists.\n"
						+ "We tried to read file %s but there was nothing.\n"
						+ "Please either move that file into that location or set the property %s",
						workspace,
						WorkspaceDsl.NAME));
			}
			StructurizrDslParser parser = new StructurizrDslParser();
			try {
				// Bad news, this absolutely requires a File object. So let's create one from our FileObject
				parser.parse(workspace.getPath().toFile());
			} catch (StructurizrDslParserException e) {
				throw new RuntimeException("Can't read workspace.dsl", e);
			}
			return parser.getWorkspace();
		} catch (FileSystemException e1) {
			throw new CantAccessPath(String.format("Can't access path %s", workspace), e1);
		}
	}

}
