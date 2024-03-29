package org.ndx.aadarchi.freemarker;

import java.io.IOException;

import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.InjectionPoint;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

public class FreemarkerTemplateProducer {
	public static class CantLoadTemplate extends RuntimeException {

		public CantLoadTemplate(String message, Throwable cause) {
			super(message, cause);
		}
		
	}
	@Produces Configuration createConfiguration() {
		Configuration returned = new Configuration();
		returned.setObjectWrapper(new DefaultObjectWrapper());
		returned.setDefaultEncoding("UTF-8");
		returned.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		returned.setIncompatibleEnhancements("2.3.20");
		return returned;
	}
	
	/**
	 * Allow to inject a template named
	 * @param injection
	 * @return
	 */
	@Produces Template produceTemplate(InjectionPoint injection, Configuration configuration) {
		Class<?> beanClass = injection.getBean().getBeanClass();
		configuration.setClassForTemplateLoading(beanClass, "/templates/"+beanClass.getSimpleName());
		String templateName = String.format("%s.ftl", injection.getMember().getName());
		try {
			return configuration.getTemplate(templateName);
		} catch (IOException e) {
			throw new CantLoadTemplate(String.format("Cant't load template %s", templateName), e);
		}
	}
}
