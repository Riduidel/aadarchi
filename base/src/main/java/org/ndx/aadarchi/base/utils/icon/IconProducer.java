package org.ndx.aadarchi.base.utils.icon;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

/**
 * This icon producer uses the icon provider to generate the icon string when possible
 * @author Nicolas
 *
 */
@ApplicationScoped
public class IconProducer {
	@Inject IconProvider iconProvider;
	
	@Produces @FontIcon String createFontIcon(InjectionPoint injectionPoint) {
        Annotated annotated = injectionPoint.getAnnotated();
        FontIcon fontIconAnnotation = annotated.getAnnotation(FontIcon.class);
        return iconProvider.getIcon(fontIconAnnotation.name(), fontIconAnnotation.set());
	}
}
