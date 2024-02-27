package org.ndx.aadarchi.maven.cdi.helper.wrappers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Set;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.context.spi.CreationalContext;
import jakarta.enterprise.inject.Typed;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.InjectionPoint;
import jakarta.inject.Qualifier;

public class CdiProducerBean<T> implements Bean<T> {
	private Method method;
	private Object hostInstance;
	private BeanManager beanManager;
	private Set<Annotation> qualifiers;
	private Set<Type> types;
	private Class<?> instanceClass;

	public CdiProducerBean(Method method, Object hostInstance, BeanManager beanManager, Type type,
			Class<?> instanceClass, Set<Annotation> qualifiers) {
		this.method = method;
		this.hostInstance = hostInstance;
		this.beanManager = beanManager;
		this.instanceClass = instanceClass;
		this.qualifiers = qualifiers;
		this.types = calcBeanTypes(type);
	}

	private Set<Type> calcBeanTypes(Type implTpye) {
		Set<Type> beanTypes = Sets.newHashSet();

		if (implTpye instanceof ParameterizedType) {
			beanTypes.add((ParameterizedType) implTpye);
		} else {
			Typed typedAnnotation = ((Class<?>) implTpye).getAnnotation(Typed.class);
			if (typedAnnotation != null) {
				for (Class<?> cls : typedAnnotation.value()) {
					beanTypes.add(cls);
				}
			} else {
				beanTypes.addAll(getTypeClasses((Class<?>) implTpye));
			}
		}
		return beanTypes;
	}

	private Set<Class<?>> getTypeClasses(Class<?> cls) {
		if (cls == null) {
			return Collections.emptySet();
		}

		Set<Class<?>> classes = Sets.newHashSet();
		classes.add(cls);
		classes.addAll(getTypeClasses(cls.getSuperclass()));
		for (Class<?> iface : cls.getInterfaces()) {
			classes.addAll(getTypeClasses(iface));
		}
		return classes;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T create(CreationalContext<T> creationalContext) {
		Object[] params = new Object[0];
		Class<?>[] parameterTypes = this.method.getParameterTypes();
		Annotation[][] parameterAnnotations = this.method.getParameterAnnotations();
		params = new Object[parameterTypes.length];
		for (int i = 0; i < parameterTypes.length; i++) {
			Set<Annotation> qualifiers = getCdiQualifiers(parameterAnnotations[i]);

			Class<?> paramType = parameterTypes[i];
			Set<Bean<?>> beans = this.beanManager.getBeans(paramType,
					qualifiers.toArray(new Annotation[qualifiers.size()]));
			if (beans.size() == 1) {
				Bean<?> bean = Iterables.get(beans, 0);
				Object reference = this.beanManager.getReference(bean, paramType,
						this.beanManager.createCreationalContext(bean));
				params[i] = reference;
			} else {
				// FIXME handle -> ambiguous results
			}
		}

		T instance = null;
		try {
			this.method.setAccessible(true);
			instance = (T) this.method.invoke(this.hostInstance, params);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return instance;
	}

	@Override
	public void destroy(T instance, CreationalContext<T> creationalContext) {
	}

	@Override
	public Set<Type> getTypes() {
		return this.types;
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

	public static Set<Annotation> getCdiQualifiers(Annotation[] annotations) {
		Set<Annotation> qualifiers = Sets.newHashSet();
		for (Annotation annotation : annotations) {
			if (annotation.annotationType().isAnnotationPresent(Qualifier.class)) {
				qualifiers.add(annotation);
			}
		}
		if (qualifiers.isEmpty()) {
			qualifiers.add(new DefaultLiteral());
		}
		return qualifiers;
	}
}
