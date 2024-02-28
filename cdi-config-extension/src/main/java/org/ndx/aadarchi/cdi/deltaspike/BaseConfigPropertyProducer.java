package org.ndx.aadarchi.cdi.deltaspike;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.logging.Logger;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.InjectionPoint;
import jakarta.inject.Inject;

public class BaseConfigPropertyProducer {
	private static final Logger logger = Logger.getLogger(BaseConfigPropertyProducer.class.getName());
	@Inject ConfigProvider configProvider;

	/**
	 * Get the value of ConfigProperty as a String
	 * @param injectionPoint the injection point to get a value for
	 * @return the value of the {@link ConfigProperty} annotation at the injection point as string.
	 */
	protected String getStringPropertyValue(InjectionPoint injectionPoint) {
		ConfigProperty property = injectionPoint.getAnnotated().getAnnotation(ConfigProperty.class);
		return configProvider.getValueOf(property);
	}

	@Produces
	@Dependent
    @ConfigProperty(name = "ignored") // we actually don't need the name
	public String produceString(InjectionPoint injectionPoint) {
		logger.info("Getting string property value of "+injectionPoint);
		return getStringPropertyValue(injectionPoint);
	}

	@Produces
	@Dependent
    @ConfigProperty(name = "ignored") // we actually don't need the name
	public BigDecimal produceBigDecimal(InjectionPoint injectionPoint) {
		return BigDecimal.valueOf(produceDouble(injectionPoint));
	}

	@Produces
	@Dependent
    @ConfigProperty(name = "ignored") // we actually don't need the name
	public BigInteger produceBigInteger(InjectionPoint injectionPoint) {
		return BigInteger.valueOf(produceLong(injectionPoint));
	}

	@Produces
	@Dependent
    @ConfigProperty(name = "ignored") // we actually don't need the name
	public Byte produceByte(InjectionPoint injectionPoint) {
		return Byte.parseByte(getStringPropertyValue(injectionPoint));
	}

	@Produces
	@Dependent
    @ConfigProperty(name = "ignored") // we actually don't need the name
	public Boolean produceBoolean(InjectionPoint injectionPoint) {
		return Boolean.parseBoolean(getStringPropertyValue(injectionPoint));
	}

	@Produces
	@Dependent
    @ConfigProperty(name = "ignored") // we actually don't need the name
	public Double produceDouble(InjectionPoint injectionPoint) {
		return Double.parseDouble(getStringPropertyValue(injectionPoint));
	}

	@Produces
	@Dependent
    @ConfigProperty(name = "ignored") // we actually don't need the name
	public Float produceFloat(InjectionPoint injectionPoint) {
		return Float.parseFloat(getStringPropertyValue(injectionPoint));
	}

	@Produces
	@Dependent
    @ConfigProperty(name = "ignored") // we actually don't need the name
	public Integer produceInteger(InjectionPoint injectionPoint) {
		return Integer.parseInt(getStringPropertyValue(injectionPoint));
	}

	@Produces
	@Dependent
    @ConfigProperty(name = "ignored") // we actually don't need the name
	public Long produceLong(InjectionPoint injectionPoint) {
		return Long.parseLong(getStringPropertyValue(injectionPoint));
	}

	@Produces
	@Dependent
    @ConfigProperty(name = "ignored") // we actually don't need the name
	public Short produceShort(InjectionPoint injectionPoint) {
		return Short.parseShort(getStringPropertyValue(injectionPoint));
	}
}
