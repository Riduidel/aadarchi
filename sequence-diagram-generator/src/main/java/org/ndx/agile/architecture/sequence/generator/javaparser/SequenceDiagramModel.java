package org.ndx.agile.architecture.sequence.generator.javaparser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.structurizr.model.Component;
import com.structurizr.model.Container;

/**
 * A model of sequence diagram that will be used to generate the effective sequence diagram
 * @author nicolas-delsaux
 *
 */
public class SequenceDiagramModel {

	public static class Builder {
		private CallInstance start;

		/**
		 * Initial builder used to bootstrap the sequence diagram
		 * @param methodRepresentation
		 * @param callGraphModel
		 * @return
		 */
		public Builder startsWith(MethodRepresentation methodRepresentation) {
			this.start = new CallInstance("", methodRepresentation);
			return this;
		}

		public SequenceDiagramModel build(CallGraphModel callGraphModel) {
			SequenceDiagramModel returned = new SequenceDiagramModel();
			returned.addCallrecursively(start, callGraphModel.classesToComponents);
			return returned;
		}
	}
	
	private static interface SequenceLine {

		String toEffectiveSequence();
		
	}
	
	private class Activation implements SequenceLine {
		public final Component component;

		public Activation(Component component) {
			super();
			this.component = component;
		}

		@Override
		public String toEffectiveSequence() {
			return String.format("activate %s", componentId(component));
		}
	}
	
	private class Deactivation implements SequenceLine {
		public final Component component;

		public Deactivation(Component component) {
			super();
			this.component = component;
		}

		@Override
		public String toEffectiveSequence() {
			return String.format("deactivate %s", componentId(component));
		}
	}
	
	private class CallRepresentation implements SequenceLine {
		public CallRepresentation(Optional<Component> from, Component to, String message) {
			super();
			this.from = from;
			this.to = to;
			this.message = message;
		}
		public final Optional<Component> from;
		public final Component to;
		public final String message;
		@Override
		public String toEffectiveSequence() {
			return String.format("%s->%s:%s", 
					from.stream().map(c -> componentId(c))
						.findFirst().orElse("user"),
					componentId(to),
					message);
		}
	}

	/**
	 * components used in that sequence diagram
	 */
	private Set<Component> components = new LinkedHashSet<>();
	private Collection<SequenceLine> calls = new ArrayList<>();
	
	/**
	 * Add call to a component and all inner calls between components to the sequence diagram
	 * @param start start call
	 * @param callGraphModel model containing all classes infos
	 */
	public void addCallrecursively(CallInstance start, Map<String, Component> classesToComponents) {
		components.add(classesToComponents.get(start.called.className));
		calls.add(new Activation(classesToComponents.get(start.called.className)));
		calls.add(callRepresentation(start, classesToComponents));
		for(CallInstance callInstance : start.called.calls) {
			addCallrecursively(callInstance, classesToComponents);
		}
		calls.add(new Deactivation(classesToComponents.get(start.called.className)));
	}
	
	private SequenceLine callRepresentation(CallInstance start, Map<String, Component> classesToComponents) {
		Optional<Component> from = start.caller.stream()
			.map(method -> classesToComponents.get(method.className))
			.findAny(); 
		return new CallRepresentation(from,
				classesToComponents.get(start.called.className), 
				start.called.signature);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		// First, show components, grouped by containers (not by systems, because it is not possible to nest boxes)
		builder.append(getParticipantsDeclaration());
		builder.append("\n\n");
		builder.append(getEffectiveSequence());
		return builder.toString();
	}

	private String getEffectiveSequence() {
		return calls.stream()
				.map(c -> c.toEffectiveSequence())
				.collect(Collectors.joining("\n"));
	}

	private String getParticipantsDeclaration() {
		StringBuilder returned = new StringBuilder("actor User as user\n");
		returned.append(components.stream()
			.collect(Collectors.groupingBy(component -> component.getContainer()))
			.entrySet()
			.stream()
			.map(entry -> getComponentsOfContainerAsParticipants(entry))
			.collect(Collectors.joining("\n")));
		return returned.toString();
	}

	private String getComponentsOfContainerAsParticipants(Entry<Container, List<Component>> entry) {
		String componentsAsParticipants = entry.getValue().stream()
			.map(component -> String.format("participant %s as %s",
					component.getName(),
					componentId(component)
					))
			.collect(Collectors.joining("\n"));
		return String.format("box %s\n%s\nend box",
				entry.getKey().getName(),
				componentsAsParticipants);
	}
	
	private String componentId(Component component) {
		return component.getCanonicalName().substring(1)
				.replace('/', '_')
				.replace('-', '_');
	}
}
