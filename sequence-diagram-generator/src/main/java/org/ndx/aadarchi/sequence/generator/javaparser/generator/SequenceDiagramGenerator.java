package org.ndx.aadarchi.sequence.generator.javaparser.generator;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.io.FileUtils;
import org.ndx.aadarchi.sequence.generator.SequenceGeneratorException;
import org.ndx.aadarchi.sequence.generator.javaparser.adapter.BlockRepresentation;
import org.ndx.aadarchi.sequence.generator.javaparser.adapter.CallGraphModel;
import org.ndx.aadarchi.sequence.generator.javaparser.adapter.CodeRepresentationVisitor;
import org.ndx.aadarchi.sequence.generator.javaparser.adapter.ForEachRepresentation;
import org.ndx.aadarchi.sequence.generator.javaparser.adapter.ForLoopRepresentation;
import org.ndx.aadarchi.sequence.generator.javaparser.adapter.IfRepresentation;
import org.ndx.aadarchi.sequence.generator.javaparser.adapter.MethodCallRepresentation;
import org.ndx.aadarchi.sequence.generator.javaparser.adapter.MethodDeclarationRepresentation;
import org.ndx.aadarchi.sequence.generator.javaparser.adapter.ObjectCreationRepresentation;
import org.ndx.aadarchi.sequence.generator.javaparser.adapter.TypeRepresentation;

import com.structurizr.model.CodeElement;
import com.structurizr.model.Component;

public class SequenceDiagramGenerator implements CodeRepresentationVisitor {

	private final File outputFolder;
	/**
	 * Stack of construction kits. This allow empty loop to generate missing
	 * diagrams, which are not added to main diagram.
	 */
	private Stack<SequenceDiagramConstructionKit> kits = new Stack<>();
	private final Map<String, Component> classesToComponents;
	private CallGraphModel callGraphModel;

	public SequenceDiagramGenerator(File outputFolder, Map<String, Component> classesToComponents, CallGraphModel callGraphModel) {
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
		boolean firstCall = kits.isEmpty();
		activateNewKit();
		if (firstCall) {
			// If it is a first call, we have to add the fake call that bootstraps the
			// diagram
			// Obviously, this requires equivalent code at endVisit
			kits.peek().activateMethodCall(methodDeclarationRepresentation);
		}
	}

	private SequenceDiagramConstructionKit activateNewKit() {
		SequenceDiagramConstructionKit newKit = new SequenceDiagramConstructionKit(classesToComponents, new LinkedHashSet<Component>());
		kits.push(newKit);
		return newKit;
	}

	private SequenceDiagramConstructionKit deactivateKit() {
		SequenceDiagramConstructionKit usedKit = kits.pop();
		if (!kits.isEmpty()) {
			kits.peek().addAll(usedKit);
		}
		return usedKit;
	}

	@Override
	public void endVisit(MethodDeclarationRepresentation methodDeclarationRepresentation) {
		SequenceDiagramConstructionKit usedKit = deactivateKit();
		if (kits.isEmpty()) {
			// See startVisit for the reason why
			usedKit.deactivateMethodCall(methodDeclarationRepresentation);
			File outputFile = new File(outputFolder, methodDeclarationRepresentation.filename + ".plantuml");
			try {
				outputFile.getParentFile().mkdirs();
				FileUtils.write(outputFile, usedKit.sequence(), "UTF-8");
			} catch (IOException e) {
				throw new SequenceGeneratorException(
						String.format("Unable to write to %s", outputFile.getAbsolutePath()), e);
			}
		}
	}

	@Override
	public void startVisit(MethodCallRepresentation methodCallRepresentation) {
		if (this.kits.peek().activateMethodCall(methodCallRepresentation)) {
			// So we're in a method call?
			// If method call target is a method of a component, then we should try to see
			// if that method
			// has an implementation.
			// And if there is an implementation, add it recursively to diagram
			MethodDeclarationRepresentation implementation = findImplementationOf(methodCallRepresentation);
			if(implementation!=null) {
				implementation.accept(this);
			}
		}
	}

	private MethodDeclarationRepresentation findImplementationOf(MethodCallRepresentation methodCallRepresentation) {
		Component component = classesToComponents.get(methodCallRepresentation.calledTypeName);
		MethodDeclarationRepresentation representation = null;
		for (CodeElement code : component.getCode()) {
			representation = callGraphModel.getClassFor(code.getType()).getMethodFor(methodCallRepresentation);
			// We may encounter null case
			// Typically when the method we're looking for is a private method in a class, and we're looking at the interface
			// This will be null.
			if(representation!=null) {
				if (!representation.getChildren().isEmpty()) {
					break;
				}
			}
		}
		return representation;
	}

	@Override
	public void endVisit(MethodCallRepresentation methodCallRepresentation) {
		this.kits.peek().deactivateMethodCall(methodCallRepresentation);
	}

	@Override
	public void startVisit(ObjectCreationRepresentation objectCreationRepresentation) {
		this.kits.peek().activateCreation(objectCreationRepresentation);
	}

	/**
	 * There is nothing to do after an object creation
	 */
	@Override
	public void endVisit(ObjectCreationRepresentation objectCreationRepresentation) {
	}

	/**
	 * Visiting a block is useful when that block is in a condition statement, to
	 * detect alternatives. But otherwise there is no interest in that ...
	 */
	@Override
	public void startVisit(BlockRepresentation blockRepresentation) {
	}

	@Override
	public void endVisit(BlockRepresentation blockRepresentation) {
	}

	@Override
	public void startVisit(ForEachRepresentation forEachRepresentation) {
		activateNewKit().activateGroup(forEachRepresentation);
	}

	@Override
	public void endVisit(ForEachRepresentation forEachRepresentation) {
		kits.peek().deactivateGroup(forEachRepresentation);
		deactivateKit();
	}

	@Override
	public void startVisit(ForLoopRepresentation forLoopRepresentation) {
		activateNewKit().activateGroup(forLoopRepresentation);
	}

	@Override
	public void endVisit(ForLoopRepresentation forLoopRepresentation) {
		kits.peek().deactivateGroup(forLoopRepresentation);
		deactivateKit();
	}

	@Override
	public void startVisit(IfRepresentation ifRepresentation) {
		activateNewKit().activateGroup(ifRepresentation);
	}

	@Override
	public void endVisit(IfRepresentation ifRepresentation) {
		kits.peek().deactivateGroup(ifRepresentation);
		deactivateKit();
	}

}
