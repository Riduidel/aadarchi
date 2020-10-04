package org.ndx.agile.architecture.sequence.generator.javaparser.generator;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.ndx.agile.architecture.sequence.generator.javaparser.adapter.GroupRepresentation;
import org.ndx.agile.architecture.sequence.generator.javaparser.adapter.MethodCallRepresentation;
import org.ndx.agile.architecture.sequence.generator.javaparser.adapter.MethodDeclarationRepresentation;
import org.ndx.agile.architecture.sequence.generator.javaparser.adapter.ObjectCreationRepresentation;

import com.structurizr.model.Component;
import com.structurizr.model.Container;

public class SequenceDiagramConstructionKit {
	private static final int SIGNATURE_WISTH = 40;
	public static final String SEQUENCE_KEY = SequenceDiagramConstructionKit.class.getName()+".Sequence";
	/**
	 * List components involved in sequence diagram, used to generate header.
	 * Notice components list is shared between all sequence construction kits of a stack.
	 * THIS IS DANGEROUS, but required to avoid multi-creations with anonymous clases.
	 */
	private Collection<Component> components;
	
	private StringBuilder methodCalls = new StringBuilder();

	private final Map<String, Component> classesToComponents;
	/**
	 * Set to true when diagram has some meaning, that's to say contains useful method calls.
	 */
	private boolean meaningful = false;

	public SequenceDiagramConstructionKit(Map<String, Component> classesToComponents, Collection<Component> components) {
		super();
		this.classesToComponents = classesToComponents;
		mapComponentsToNames(classesToComponents.values());
		this.components = components;
	}

	/**
	 * Sets a hidden property (if not already present) in each component assigning it a unique name
	 * usable for sequence diagrams.
	 * I hope those component names will be very similar to components real names.
	 * @param values
	 */
	private void mapComponentsToNames(Collection<Component> values) {
		Collection<String> components = new HashSet<String>();
		// First, get already defined names
		for(Component c : values) {
			if(c.getProperties().containsKey(SEQUENCE_KEY)) {
				components.add(c.getProperties().get(SEQUENCE_KEY));
			}
		}
		for(Component c : values) {
			if(!c.getProperties().containsKey(SEQUENCE_KEY)) {
				// What is the easiest way to get a unique/non stupd name? Suffix component name with an int!
				for (int i = 0; i < Integer.MAX_VALUE && !c.getProperties().containsKey(SEQUENCE_KEY); i++) {
					String potentialName = String.format("%s_%d", c.getName(), i);
					if(!components.contains(potentialName)) {
						components.add(potentialName);
						c.addProperty(SEQUENCE_KEY, potentialName);
					}
				}
			}
		}
	}

	private String getParticipantsDeclaration() {
		Map<Container, List<Component>> componentsPerContainers = components.stream()
			.collect(Collectors.groupingBy(component -> component.getContainer()));
		StringBuilder returned = new StringBuilder();
		returned.append(componentsPerContainers
			.entrySet()
			.stream()
			.map(entry -> getComponentsOfContainerAsParticipants(entry))
			.collect(Collectors.joining("\n")));
		return returned.toString();
	}

	private String getComponentsOfContainerAsParticipants(Entry<Container, List<Component>> entry) {
		String componentsAsParticipants = entry.getValue().stream()
			.map(this::componentAsParticipant)
			.collect(Collectors.joining("\n"));
		return String.format("box %s\n%s\nend box",
				entry.getKey().getName(),
				componentsAsParticipants);
	}

	private String componentAsParticipant(Component component) {
		return String.format("participant %s as %s",
				component.getName(),
				componentId(component)
				);
	}
	
	private String componentId(Component component) {
		if(component.getProperties().containsKey(SEQUENCE_KEY)) {
			return component.getProperties().get(SEQUENCE_KEY);
		} else {
			return component.getCanonicalName().substring(1)
					.replace('/', '_')
					.replace('-', '_');
		}
	}
	
	/**
	 * Activates a method ccall
	 * @param methodCallRepresentation
	 * @return true if that call is to a component (which implies more calls to fetch)
	 */
	public boolean activateMethodCall(MethodCallRepresentation methodCallRepresentation) {
		String fromType = methodCallRepresentation.containerOfType(MethodDeclarationRepresentation.class)
				.stream()
				.map(declaration -> declaration.className)
				.findFirst().orElse("[");
		String toType = methodCallRepresentation.calledTypeName;
		if(classesToComponents.containsKey(toType)) {
			Component toComponent = classesToComponents.get(toType);
			components.add(toComponent);
			if(fromType.equals("[")) {
				methodCalls.append(fromType);
			} else {
				Component fromComponent = classesToComponents.get(fromType);
				methodCalls.append(componentId(fromComponent));
			}
			methodCalls.append("->")
					.append(componentId(toComponent))
					.append(':')
					.append(StringUtils.abbreviate(methodCallRepresentation.calledSignature, "...", SIGNATURE_WISTH))
					.append('\n');
			methodCalls.append("activate ").append(componentId(toComponent)).append('\n');
			meaningful = true;
			return true;
		} else {
			return false;
		}
	}

	public void deactivateMethodCall(MethodCallRepresentation methodCallRepresentation) {
		String toType = methodCallRepresentation.calledTypeName;
		if(classesToComponents.containsKey(toType)) {
			methodCalls.append("deactivate ").append(componentId(classesToComponents.get(toType))).append('\n');
		}
	}

	public String sequence() {
		StringBuilder returned = new StringBuilder();
		// First output header built from components
		returned.append(getParticipantsDeclaration());
		returned.append("\n\n");
		// Then output chain of method calls and other accumulated elements
		returned.append(methodCalls);
		return returned.toString();
	}

	/**
	 * This is a special method used to bootstrap the sequence diagram
	 * by creating a fake call from the user to the first component
	 * @param methodDeclarationRepresentation
	 */
	public void activateMethodCall(MethodDeclarationRepresentation methodDeclarationRepresentation) {
		if(classesToComponents.containsKey(methodDeclarationRepresentation.className)) {
			Component toComponent = classesToComponents.get(methodDeclarationRepresentation.className);
			components.add(toComponent);
			methodCalls.append("[->")
				.append(componentId(toComponent))
				.append(':')
				.append(StringUtils.abbreviate(methodDeclarationRepresentation.signature, "...", SIGNATURE_WISTH))
				.append('\n');
			methodCalls.append("activate ").append(componentId(toComponent)).append('\n');
		}
	}

	public void deactivateMethodCall(MethodDeclarationRepresentation methodDeclarationRepresentation) {
		String toType = methodDeclarationRepresentation.className;
		if(classesToComponents.containsKey(toType)) {
			methodCalls.append("deactivate ").append(componentId(classesToComponents.get(toType))).append('\n');
		}
	}
	
	public void createIfNeeded(ObjectCreationRepresentation objectCreationRepresentation, 
			BiConsumer<ObjectCreationRepresentation, Component> consumer) {
		if(classesToComponents.containsKey(objectCreationRepresentation.className)) {
			Component toComponent = classesToComponents.get(objectCreationRepresentation.className);
			if(!components.contains(toComponent)) {
				consumer.accept(objectCreationRepresentation, toComponent);
			}
		}
	}

	public void activateCreation(ObjectCreationRepresentation objectCreationRepresentation) {
		createIfNeeded(
				objectCreationRepresentation,
				(objectCreation, toComponent) ->  {
					components.add(toComponent);
					methodCalls.append("create ")
					.append(componentId(toComponent))
					.append('\n');
					// Don't forget to add the "new" call
					Optional<MethodDeclarationRepresentation> declaration = objectCreation.containerOfType(MethodDeclarationRepresentation.class);
					declaration.stream()
					.forEach(decl -> {
						Component fromComponent = classesToComponents.get(decl.className);
						methodCalls.append(componentId(fromComponent))
						.append("->")
						.append(componentId(toComponent))
						.append(":new\n");
						
					});
				});
	}

	/**
	 * Include in this construction kit all elements from the usedKit one.
	 * Notice we add diagram only if meaningfull, 
	 * which is easy to see: meaningless diagrams contains no components
	 * @param usedKit construction kit to add
	 */
	public void addAll(SequenceDiagramConstructionKit usedKit) {
		if(usedKit.meaningful) {
			components.addAll(usedKit.components);
			methodCalls.append('\n').append(usedKit.methodCalls);
		}
	}

	public void activateGroup(GroupRepresentation groupRepresentation) {
		methodCalls.append("group ").append(groupRepresentation.getGroupName()).append('\n');
	}

	public void deactivateGroup(GroupRepresentation forEachRepresentation) {
		methodCalls.append("end").append('\n');
	}
}
