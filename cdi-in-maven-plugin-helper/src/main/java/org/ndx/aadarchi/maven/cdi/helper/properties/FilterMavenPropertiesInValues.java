package org.ndx.aadarchi.maven.cdi.helper.properties;

import java.util.logging.Logger;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.PluginParameterExpressionEvaluator;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.ndx.aadarchi.cdi.deltaspike.ConfigFilter;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class FilterMavenPropertiesInValues implements ConfigFilter {
	private static final Logger logger = Logger.getLogger(FilterMavenPropertiesInValues.class.getName());
	
	private PluginParameterExpressionEvaluator evaluator;

	public FilterMavenPropertiesInValues() {
		super();
	}

	@Inject
	public void createMavenExpressionEvaluator(MavenSession mavenSession, MojoExecution execution) {
		evaluator = new PluginParameterExpressionEvaluator(mavenSession, execution);
	}

	@Override
	public String filterValue(String key, String value) {
		logger.info("Filtering value "+key);
		try {
			return evaluator.evaluate(value).toString();
		} catch (ExpressionEvaluationException e) {
			return value;
		}
	}

	@Override
	public String filterValueForLog(String key, String value) {
		if (key.contains("password")) {
			return "***************";
		} else {
			return filterValue(key, value);
		}

	}

}
