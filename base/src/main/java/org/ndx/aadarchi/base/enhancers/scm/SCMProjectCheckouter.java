package org.ndx.aadarchi.base.enhancers.scm;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.ndx.aadarchi.base.OutputBuilder;
import org.ndx.aadarchi.base.enhancers.ModelElementAdapter;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys.Scm.CheckoutEnabled;

import com.structurizr.annotation.UsesComponent;
import com.structurizr.model.StaticStructureElement;

/**
 * Checkout projects mentionned in elements properties in the desired location.
 * WARNING: This enhancer is opt-in : it has to be activated by setting {@link CheckoutEnabled#VALUE}
 * property, either on model, or on an element
 * 
 * @author Nicolas
 *
 */
public class SCMProjectCheckouter extends SCMModelElementAdapter {

	@Inject
	@ConfigProperty(name = ModelElementKeys.Scm.CheckoutLocation.NAME, defaultValue = ModelElementKeys.Scm.CheckoutLocation.VALUE)
	private String defaultCheckoutLocation;
	@Inject
	@ConfigProperty(name = ModelElementKeys.Scm.CheckoutEnabled.NAME, defaultValue = ModelElementKeys.Scm.CheckoutEnabled.VALUE)
	private String defaultCheckoutProject;

	/**
	 * We put this enhancer as first, in order for other elements to reuse the obtained project
	 */
	@Override
	public int priority() {
		return 0;
	}

	@Override
	protected void processElement(StaticStructureElement element, OutputBuilder builder) {
		Map<String, String> properties = element.getProperties();
		if( Boolean.parseBoolean(properties.getOrDefault(ModelElementKeys.Scm.CheckoutEnabled.NAME, defaultCheckoutProject))) {
			if (properties.containsKey(ModelElementKeys.Scm.PROJECT)) {
				String project = properties.get(ModelElementKeys.Scm.PROJECT);
				if(project!=null) {
					File checkoutLocation = new File(properties.getOrDefault(ModelElementKeys.Scm.CheckoutLocation.NAME, defaultCheckoutLocation),
							element.getName());
					withHandlerFor(project)
						.ifPresentOrElse(
								scmHandler -> this.checkoutWithHandler(scmHandler, project, checkoutLocation, element),
							() -> logger.warning(String.format("There is no SCM Handler for %s", project)));
				}
			}
		}
	}

	private void checkoutWithHandler(SCMHandler scmHandler, String project, File checkoutLocation, StaticStructureElement element) {
			try {
				scmHandler.checkout(project, checkoutLocation);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
}
