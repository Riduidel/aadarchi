package org.ndx.aadarchi.base.utils.icon;

import java.util.function.Supplier;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys;

/**
 * A specialized component providing icons from font-awesome when needed
 * (or not providing anything if font awesome usage is disabled
 * @author Nicolas
 *
 */
@com.structurizr.annotation.Component(technology = "Java, CDI")
@ApplicationScoped
public class IconProvider {
	private boolean enabled;
	@Inject
	public void setEnabled(@ConfigProperty(name=ModelElementKeys.ConfigProperties.DisabledFontIcons.NAME,
		defaultValue = ModelElementKeys.ConfigProperties.DisabledFontIcons.VALUE) boolean disabled) {
		this.enabled = !disabled;
	}
	public String getIcon(String icon) {
		return areIconEnabled(() -> String.format("icon:%s[]", icon),
				() -> "");
	}
	private String areIconEnabled(Supplier<String> withIcon, Supplier<String> withoutIcon) {
		if(enabled) {
			return withIcon.get();
		} else {
			return withoutIcon.get();
		}
	}
	public String getIcon(String icon, String set) {
		return areIconEnabled(() -> String.format("icon:%s[set=%s]", icon, set),
				() -> "");
	}
}
