package org.ndx.agile.architecture.tickets;

import java.io.IOException;

import org.ndx.agile.architecture.base.AgileArchitectureException;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

public class FreemarkerTemplateProducer {
	public static class CantLoadTemplate extends AgileArchitectureException {

		public CantLoadTemplate(String message, Throwable cause) {
			super(message, cause);
		}
		
	}
	Configuration createConfiguration() {
		Configuration returned = new Configuration();
		returned.setObjectWrapper(new DefaultObjectWrapper());
		returned.setDefaultEncoding("UTF-8");
		returned.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		returned.setIncompatibleEnhancements("2.3.20");
		return returned;
	}
	
	public Template produceTemplate(Configuration configuration, Class<?> beanClass,
			String templateId) {
		String classSimpleName = beanClass.getSimpleName();
		configuration.setClassForTemplateLoading(beanClass, "/templates/"+classSimpleName);
		String templateName = String.format("%s.ftl", templateId);
		try {
			return configuration.getTemplate(templateName);
		} catch (IOException e) {
			throw new CantLoadTemplate(String.format("Cant't load template %s", templateName), e);
		}
	}
}
