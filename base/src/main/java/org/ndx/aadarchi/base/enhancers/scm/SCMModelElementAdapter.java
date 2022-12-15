package org.ndx.aadarchi.base.enhancers.scm;

import java.util.Optional;
import java.util.logging.Logger;

import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

import org.ndx.aadarchi.base.enhancers.ModelElementAdapter;

import com.structurizr.annotation.UsesComponent;

public abstract class SCMModelElementAdapter extends ModelElementAdapter {

	@Inject
	protected Logger logger;
	@Inject
	@UsesComponent(description = "Get SCM infos")
	protected Instance<SCMHandler> scmHandlers;
	protected Optional<SCMHandler> withHandlerFor(String elementProject) {
		return scmHandlers.stream()
				.filter(handler -> handler.canHandle(elementProject))
				.findFirst()
				;
	}

}
