package org.ndx.aadarchi.maven.cdi.helper.properties;

import jakarta.inject.Inject;

import org.apache.deltaspike.core.api.config.Filter;
import org.apache.deltaspike.core.api.projectstage.ProjectStage;
import org.apache.deltaspike.core.spi.config.ConfigFilter;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.PluginParameterExpressionEvaluator;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;

@Filter
public class FilterMavenPropertiesInValues implements ConfigFilter {
	
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
		try {
			return (String) evaluator.evaluate(value);
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
