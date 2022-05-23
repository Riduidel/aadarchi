package org.ndx.agile.architecture.documentation.system.maven.cdi.helper;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.deltaspike.core.api.config.Source;
import org.apache.deltaspike.core.api.projectstage.ProjectStage;
import org.apache.deltaspike.core.spi.config.ConfigSource;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.PluginParameterExpressionEvaluator;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;

@Source
public class ExposeMavenPropertiesAsConfigproperties implements ConfigSource {
	private static final Logger logger = Logger.getLogger(ExposeMavenPropertiesAsConfigproperties.class.getName());

	private static final int DELTASPIKE_PRIORITY = 1000;

	private PluginParameterExpressionEvaluator evaluator;

	public ExposeMavenPropertiesAsConfigproperties() {
		super();
	}
	
	@Inject ProjectStage projectStage;
	
	@Inject
	public void createMavenExpressionEvaluator(MavenSession mavenSession, MojoExecution execution) {
		evaluator = new PluginParameterExpressionEvaluator(mavenSession, execution);
	}

	@Override
	public String getConfigName() {
		return "maven-properties";
	}

	@Override
	public Map<String, String> getProperties() {
		return null;
	}

	@Override
	public String getPropertyValue(String key) {
		try {
			// Property key may contain a suffix containing the DeltaSpike ProjectStage value.
			// If so, remove it prior to evaluation
			if(key.endsWith(projectStage.toString())) {
				key = key.substring(0, key.indexOf("."+projectStage.toString()));
			}
			// Seems like evaluator wants the "${" maven uses to detect variables, so let's add it (if not already present)
			if(!key.contains("${")) {
				key = "${"+key+"}";
			}
			Object value = evaluator.evaluate(key);
			// I'm sorry, but Maven expression evaluator return null when evaluation fails. This is unfortunate.
			if(value==null)
				return null;
			return value.toString();
		} catch (Exception e) {
			logger.log(Level.FINE, String.format("Unable to evaluate property %s", key), e);
			return null;
		}
	}

	@Override
	public boolean isScannable() {
		return true;
	}

	@Override
	public int getOrdinal() {
		return DELTASPIKE_PRIORITY;
	}
}
