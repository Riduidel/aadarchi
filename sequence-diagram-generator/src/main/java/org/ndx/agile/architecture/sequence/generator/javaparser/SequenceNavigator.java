package org.ndx.agile.architecture.sequence.generator.javaparser;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;

import com.github.javaparser.ast.CompilationUnit;
import com.structurizr.model.Component;

public class SequenceNavigator {
	private static final Logger logger =Logger.getLogger(SequenceNavigator.class.getName());


	private Map<String, CompilationUnit> allSources;
	private Map<String, Component> codeToComponents;
	private CallGraphModel model;

	public SequenceNavigator(Map<String, CompilationUnit> parseAllSources, Map<String, Component> codeToComponents) {
		this.allSources = parseAllSources;
		this.codeToComponents = codeToComponents;
		this.model = new CallGraphModel(codeToComponents);
	}

	/**
	 * Analyze all calls in code components
	 * @param list 
	 */
	public void analyzeCalls(List<String> list) {
		for(String type : list) {
			analyze(type);
		}
		// Also analyze all unknown classes until they're not any unknown one
		while(!model.unknownClasses.isEmpty()) {
			analyze(model.unknownClasses.first());
		}
	}

	private void analyze(String type) {
		CompilationUnit compilationUnit = allSources.get(type);
		analyze(compilationUnit);
	}

	private void analyze(CompilationUnit compilationUnit) {
		compilationUnit.accept(new JavaParserVisitorForBuildingCallGraph(), model);
	}

	/**
	 * Generate a clean sequence diagram for the given method of the component
	 * @param component model component this method is defined in
	 * @param method method for which we want to generate a diagram
	 * @param destination destination folder where diagram will be generated
	 */
	public void generatePlantUMLDiagramFor(Component component, Method method, File destination) {
		File outputFolder = new File(destination, component.getCanonicalName().substring(1));
		outputFolder.mkdirs();
		// Now create a file name from method name
		File outputFile = new File(outputFolder, methodToFileName(method)+".plantuml");
		try {
			FileUtils.write(outputFile, model.toSequenceDiagram(method));
		} catch (IOException e) {
			logger.log(Level.SEVERE, String.format("Unable to write sequence diagram %s", outputFile), e);
		}
	}

	private String methodToFileName(Method method) {
		return String.format("%s_%s%s", 
				method.getDeclaringClass().getSimpleName(),
				method.getName(),
				Stream.of(method.getParameters())
					.map(parameter -> parameter.getType().getSimpleName())
					.collect(Collectors.joining("_", "___", ""))
				);
	}
}
