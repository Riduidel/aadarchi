package org.ndx.agile.architecture.documentation.system.maven.plugin.cdi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A special CDI producer annotation that must be used if you want to declare fields or methods of the Mojo as
 * producers.<br>
 * <b>Note</b> that it is not possible to use {@code @javax.enterprise.inject.Produces} as the producer annotation in
 * your Mojo since this would activate Maven's own pseudo CDI implementation. All other beans that are created by CDI
 * are then able to use {@code @Produces} as usual.
 *
 * @author <a href="mailto:stanley.hillner@itemis.de">Stanley Hillner</a>
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.FIELD })
public @interface MojoProduces {
}