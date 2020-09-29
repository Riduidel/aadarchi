package org.ndx.agile.architecture.sequence.generator;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.ndx.agile.architecture.base.OutputBuilder;
import org.ndx.agile.architecture.sequence.generator.javaparser.PlantUMLDiagramBuilder;
import org.ndx.agile.architecture.sequence.generator.javaparser.PlantUMLDiagramModel;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.ArrayType;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;
import com.structurizr.model.Component;
import com.structurizr.model.Container;

public class SequenceNavigator {
	private static class TypeToString extends GenericVisitorAdapter<String, Void> {
		@Override
		public String visit(ArrayType n, Void arg) {
			return n.getElementType().accept(this, null)+"[]";
		}
		@Override
		public String visit(ClassOrInterfaceType n, Void arg) {
			return n.resolve().getQualifiedName();
		}
		@Override
		public String visit(PrimitiveType n, Void arg) {
			return n.asString();
		}
	}
	private static final Logger logger =Logger.getLogger(SequenceNavigator.class.getName());
	/**
	 * Once classes are read, this map links each class name to the compilation unit
	 * in which it is the primary type
	 */
	private Map<String, CompilationUnit> allSources;
	private Map<String, Component> codeToComponent;

	public void setAllSources(Map<String, CompilationUnit> parseAllSources) {
		allSources = parseAllSources;
	}

	/**
	 * To generate that sequence diagram, we need to 1. Get source code of method
	 * (unless method is abstract, obviously) 2. Read that code 3. Dive into each
	 * Java construct 4. If we find an element corresponding to a known component,
	 * link that! 5. Explore recursively to the limit - but with loop detection 6.
	 * Write that out (easy, no ?)
	 * 
	 * @param component
	 * @param builder
	 * @param type
	 * @param method
	 */
	public void generatePlantUMLSequenceDiagramFor(Component component, OutputBuilder builder, String type,
			Method method) {
		CompilationUnit cu = allSources.get(type);
		// Notice class name is simple in that case, so we don't have to search for qualified name (which is our input value)
		String simpleType = type.substring(type.lastIndexOf('.')+1);
		Optional<ClassOrInterfaceDeclaration> classDeclaration = cu.getClassByName(simpleType);
		Optional<ClassOrInterfaceDeclaration> interfaceDeclaration = cu.getInterfaceByName(simpleType);
		if (classDeclaration.isPresent()) {
			generatePlantUMLSequenceDiagramFor(component, builder, classDeclaration.get(), method);
		} else if (interfaceDeclaration.isPresent()) {
			generatePlantUMLSequenceDiagramFor(component, builder, interfaceDeclaration.get(), method);
		}
	}

	private void generatePlantUMLSequenceDiagramFor(Component component, OutputBuilder builder,
			ClassOrInterfaceDeclaration classOrInterfaceDeclaration, Method method) {
		String realRarametersSignature = getParametersSignatureOf(method);
		for(MethodDeclaration methodDeclaration : classOrInterfaceDeclaration.getMethodsByName(method.getName())) {
			// The easiest thing to check is that the method must have a body for a sequence diagram to be signifiant.
			// Let's check that first!
			if(methodDeclaration.getBody().isPresent()) {
				String declaredMethodSignature = getParametersSignatureOf(methodDeclaration);
				if(realRarametersSignature.equals(declaredMethodSignature)) {
					// We assume it's the same method!
					generatePlantUMLSequenceDiagramFor(component, builder, methodDeclaration, method);
					// Make sure no other method is scanned
					return;
				}
			}
		}
		logger.warning(String.format("We didn't found any source for method %s in declaration of %s", 
				method,
				classOrInterfaceDeclaration.getName()));
	}

	/**
	 * Now we have the method, it is time to browse its body recursively, adding elements along the way
	 * @param component
	 * @param builder
	 * @param methodDeclaration
	 * @param method
	 */
	private void generatePlantUMLSequenceDiagramFor(Component component, OutputBuilder builder,MethodDeclaration methodDeclaration, Method method) {
		logger.info(String.format("Found method %s to be\n%s", method, methodDeclaration));
		methodDeclaration.getBody().ifPresent(body -> { 
			PlantUMLDiagramModel  diagramModel = body.accept(new PlantUMLDiagramBuilder(), new PlantUMLDiagramModel()); 
		});
	}

	private String getParametersSignatureOf(MethodDeclaration methodDeclaration) {
		return methodDeclaration.getParameters().stream()
				.map(parameter -> parameter.getType().accept(new TypeToString(), null))
				.collect(Collectors.joining(","));
	}

	private String getParametersSignatureOf(Method method) {
		return Stream.of(method.getParameters())
				.map(parameter -> parameter.getAnnotatedType().toString())
				.collect(Collectors.joining(","));
	}

	public void setCodeToComponents(Map<String, Component> codeToComponents) {
		this.codeToComponent = codeToComponents;
	}
}
