package org.ndx.aadarchi.maven.cdi.helper.wrappers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;

public class CdiBeanWrapper<T> implements Bean<T> {

  private T instance;
  private Set<Annotation> qualifiers;
  private Type type;
  private Class<?> instanceClass;

  public CdiBeanWrapper(T instance, Type type, Class<?> instanceClass, Set<Annotation> qualifiers) {
    this.instance = instance;
    this.type = type;
    this.instanceClass = instanceClass;
    this.qualifiers = qualifiers;
  }

  @Override
  public T create(CreationalContext<T> creationalContext) {
    return this.instance;
  }

  @Override
  public void destroy(T instance, CreationalContext<T> creationalContext) {
  }

  @Override
  public Set<Type> getTypes() {
    return Collections.singleton(this.type);
  }

  @Override
  public Set<Annotation> getQualifiers() {
    return this.qualifiers;
  }

  @Override
  public Class<? extends Annotation> getScope() {
    return Dependent.class;
  }

  @Override
  public String getName() {
    return null;
  }

  @Override
  public Set<Class<? extends Annotation>> getStereotypes() {
    return Collections.emptySet();
  }

  @Override
  public boolean isAlternative() {
    return false;
  }

  @Override
  public Class<?> getBeanClass() {
    return this.instanceClass;
  }

  @Override
  public Set<InjectionPoint> getInjectionPoints() {
    return Collections.emptySet();
  }

  @Override
  public boolean isNullable() {
    return true;
  }
}
