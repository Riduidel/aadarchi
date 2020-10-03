package org.ndx.agile.architecture.sequence.generator.javaparser.generator;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.ndx.agile.architecture.sequence.generator.SequenceGeneratorException;
import org.ndx.agile.architecture.sequence.generator.javaparser.adapter.BlockRepresentation;
import org.ndx.agile.architecture.sequence.generator.javaparser.adapter.CallGraphModel;
import org.ndx.agile.architecture.sequence.generator.javaparser.adapter.CodeRepresentationVisitor;
import org.ndx.agile.architecture.sequence.generator.javaparser.adapter.ForEachRepresentation;
import org.ndx.agile.architecture.sequence.generator.javaparser.adapter.ForLoopRepresentation;
import org.ndx.agile.architecture.sequence.generator.javaparser.adapter.IfRepresentation;
import org.ndx.agile.architecture.sequence.generator.javaparser.adapter.MethodCallRepresentation;
import org.ndx.agile.architecture.sequence.generator.javaparser.adapter.MethodDeclarationRepresentation;
import org.ndx.agile.architecture.sequence.generator.javaparser.adapter.ObjectCreationRepresentation;
import org.ndx.agile.architecture.sequence.generator.javaparser.adapter.TypeRepresentation;

import com.structurizr.model.CodeElement;
import com.structurizr.model.Component;

public class SequenceDiagramGenerator implements CodeRepresentationVisitor {

	private final File outputFolder;
	private SequenceDiagramConstructionKit kit;
	private final Map<String, Component> classesToComponents;
	/**
	 * Deepness of method call declaration.
	 * THis allows us to emit consistent method call sequence diagrams even when multiple method declarations are met
	 */
	private int deepness;
	private CallGraphModel callGraphModel;
	
	public SequenceDiagramGenerator(File outputFolder, 
			Map<String, Component> classesToComponents,
			CallGraphModel callGraphModel) {
		this.outputFolder = outputFolder;
		this.classesToComponents = classesToComponents;
		this.callGraphModel = callGraphModel;
	}

	/**
	 * When starting to visit a class, we initialize the various elements
	 */
	@Override
	public void startVisit(TypeRepresentation classRepresentation) {
	}

	@Override
	public void endVisit(TypeRepresentation classRepresentation) {
	}

	@Override
	public void startVisit(MethodDeclarationRepresentation methodDeclarationRepresentation) {
		if(kit==null) {
			kit = new SequenceDiagramConstructionKit(classesToComponents);
			kit.activateMethodCall(methodDeclarationRepresentation);
		}
		deepness++;
	}

	@Override
	public void endVisit(MethodDeclarationRepresentation methodDeclarationRepresentation) {
		deepness--;
		if(deepness==0) {
			kit.deactivateMethodCall(methodDeclarationRepresentation);
			File outputFile = new File(outputFolder, methodDeclarationRepresentation.filename+".plantuml");
			try {
				outputFile.getParentFile().mkdirs();
				FileUtils.write(outputFile, this.kit.sequence(), "UTF-8");
			} catch (IOException e) {
				throw new SequenceGeneratorException(String.format("Unable to write to %s", outputFile.getAbsolutePath()), e);
			} finally {
				kit = null;
			}
		}
	}

	@Override
	public void startVisit(MethodCallRepresentation methodCallRepresentation) {
		if(this.kit.activateMethodCall(methodCallRepresentation)) {
			// So we're in a method call?
			// If method call target is a method of a component, then we should try to see if that method
			// has an implementation.
			// And if there is an implementation, add it recursively to diagram
			MethodDeclarationRepresentation implementation = findImplementationOf(methodCallRepresentation);
			implementation.accept(this);
		}
	}

	private MethodDeclarationRepresentation findImplementationOf(MethodCallRepresentation methodCallRepresentation) {
		Component component = classesToComponents.get(methodCallRepresentation.calledTypeName);
		MethodDeclarationRepresentation representation = null; 
		for(CodeElement code : component.getCode()) {
			representation = callGraphModel.getClassFor(methodCallRepresentation.calledTypeName)
					.getMethodFor(methodCallRepresentation);
			if(!representation.getChildren().isEmpty()) {
				break;
			}
		}
		return representation;
	}

	@Override
	public void endVisit(MethodCallRepresentation methodCallRepresentation) {
		this.kit.deactivateMethodCall(methodCallRepresentation);
	}

	@Override
	public void startVisit(ObjectCreationRepresentation objectCreationRepresentation) {
		this.kit.activateCreation(objectCreationRepresentation);
	}

	/**
	 * There is nothing to do after an object creation
	 */
	@Override
	public void endVisit(ObjectCreationRepresentation objectCreationRepresentation) {}

	/**
	 * Visiting a block is useful when that block is in a condition statement,
	 * to detect alternatives. But otherwise there is no interest in that ...
	 */
	@Override
	public void startVisit(BlockRepresentation blockRepresentation) {
	}

	@Override
	public void endVisit(BlockRepresentation blockRepresentation) {}

	@Override
	public void startVisit(ForEachRepresentation forEachRepresentation) {
//		kit.activateForLoop(forEachRepresentation);
	}

	@Override
	public void endVisit(ForEachRepresentation forEachRepresentation) {
//		kit.deactivateForLoop(forEachRepresentation);
	}

	@Override
	public void startVisit(ForLoopRepresentation forLoopRepresentation) {
//		kit.activateForLoop(forLoopRepresentation);
	}

	@Override
	public void endVisit(ForLoopRepresentation forLoopRepresentation) {
//		kit.deactivateForLoop(forLoopRepresentation);
	}

	@Override
	public void startVisit(IfRepresentation ifRepresentation) {
//		kit.activateIf(ifRepresentation);
	}

	@Override
	public void endVisit(IfRepresentation ifRepresentation) {
//		kit.deactivateIf(ifRepresentation);
	}

}
