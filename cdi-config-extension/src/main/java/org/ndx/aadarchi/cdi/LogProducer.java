package org.ndx.aadarchi.cdi;

import java.util.logging.Logger;

import org.ndx.aadarchi.cdi.deltaspike.BaseConfigPropertyProducer;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.InjectionPoint;

@ApplicationScoped
public class LogProducer extends BaseConfigPropertyProducer {

	@Produces
	@Dependent
	public Logger produceLogger(InjectionPoint injectionPoint) {
		return Logger.getLogger(injectionPoint.getBean().getBeanClass().getName());
	}
}
