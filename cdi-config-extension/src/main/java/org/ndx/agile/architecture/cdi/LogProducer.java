package org.ndx.agile.architecture.cdi;

import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.apache.deltaspike.core.spi.config.BaseConfigPropertyProducer;

@ApplicationScoped
public class LogProducer extends BaseConfigPropertyProducer {

	@Produces
	@Dependent
	public Logger produceLogger(InjectionPoint injectionPoint) {
		return Logger.getLogger(injectionPoint.getBean().getBeanClass().getName());
	}
}
