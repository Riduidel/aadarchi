package org.ndx.aadarchi.tickets;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.ndx.aadarchi.base.AgileArchitectureSection;
import org.ndx.aadarchi.base.Enhancer;
import org.ndx.aadarchi.base.OutputBuilder;
import org.ndx.aadarchi.base.enhancers.ModelElementAdapter;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys.ConfigProperties.Force;
import org.ndx.aadarchi.base.enhancers.tickets.Ticket;
import org.ndx.aadarchi.base.enhancers.tickets.TicketStatus;
import org.ndx.aadarchi.base.enhancers.tickets.TicketsHandler;

import com.structurizr.annotation.Component;
import com.structurizr.model.Element;
import com.structurizr.model.StaticStructureElement;

import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * Generates a report with the important decisions of the project
 * 
 * @author nicolas-delsaux
 *
 */
@Component
@ApplicationScoped
public class ADRExtractor 
	extends ModelElementAdapter 
	implements Enhancer {
	public static class ByStatusThenDate implements Comparator<Ticket> {
		private static Comparator<Ticket> delegate =
				Comparator.comparing(Ticket::getStatus).reversed()
					.thenComparing(Comparator.comparing(ticket -> ticket.getDateOf(TicketStatus.CLOSED).orElseGet(() -> new Date(0))))
					.thenComparing(Comparator.comparing(ticket -> ticket.getDateOf(TicketStatus.OPEN).orElseGet(() -> new Date(0))))
					; 

		@Override
		public int compare(Ticket o1, Ticket o2) {
			return delegate.compare(o1, o2);
		}
		
	}
	
	public static final String AGILE_ARCHITECTURE_TICKETS_ADR_LABEL = ModelElementKeys.PREFIX+"tickets.adr.label";
	public static final String AGILE_ARCHITECTURE_TICKETS_PROJECT = ModelElementKeys.PREFIX+"tickets.project";
	@Inject
	@ConfigProperty(name = Force.NAME, defaultValue=Force.VALUE)
	boolean force;
	@Inject
	Instance<TicketsHandler> ticketsHandlers;
	@Inject
	Logger logger;
	@Inject Template decision;
	@Inject Template decisionList;

	@Override
	public boolean isParallel() {
		return false;
	}

	@Override
	public int priority() {
		return TOP_PRIORITY_FOR_INTERNAL_ENHANCERS;
	}

	private void writeArchitectureDecisionsUsing(Element element, String project, String label, TicketsHandler handler, OutputBuilder builder) {
		File output = builder.outputFor(AgileArchitectureSection.decision_log, element, this, "adoc");
		if(force) {
			output.delete();
		}
		Collection<Ticket> tickets = handler.getTicketsTagged(project, label);
		// Write each decision
		Map<Ticket, String> ticketsTexts = tickets.stream()
			.sorted(new ByStatusThenDate())
			.collect(Collectors.toMap(Function.identity(), 
					t -> writeArchitectureDecisionTicket(builder, element, t)));
		try {
			try(FileWriter writer = new FileWriter(output)) {
				decisionList.process(new IndexModel(handler.getIssuesUrl(project),
						handler.getProjectName(project), 
						label,
						ticketsTexts), 
					writer);
			}
		} catch(IOException | TemplateException e) {
			throw new UnableToCreateDecisionLog(String.format("Can't write decision log index to file %s", 
						output), 
					e);
		}
		// Make sure the decision record uses that index
	}

	/**
	 * Write the asciidoc file containing the useful informations
	 * @param builder 
	 * @param element 
	 * @param ticket
	 * @param parentDirectory
	 * @return
	 */
	private String writeArchitectureDecisionTicket(OutputBuilder builder, Element element, Ticket toWrite) {
		Date date = toWrite.getLastDate();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd@HH-mm-ss");
		File output = builder.outputFor(AgileArchitectureSection.decision_log, element, this,
				String.format("decision.%s.%s.adoc", toWrite.getStatus(), 
						format.format(date)
					));
		output.getParentFile().mkdirs();
		try {
			try(FileWriter writer = new FileWriter(output)) {
				decision.process(toWrite, writer);
				return writer.toString();
			}
		} catch(IOException | TemplateException e) {
			throw new UnableToCreateDecisionLog(String.format("Can't write ticket %s", 
						toWrite), 
					e);
		}
	}

	@Override
	protected void processElement(StaticStructureElement element, OutputBuilder builder) {
		if (element.getProperties().containsKey(AGILE_ARCHITECTURE_TICKETS_PROJECT)) {
			String ticketsProject = element.getProperties().get(AGILE_ARCHITECTURE_TICKETS_PROJECT);
			Optional<TicketsHandler> usableHandler = ticketsHandlers.stream()
					.filter(handler -> handler.canHandle(ticketsProject)).findFirst();
			if (usableHandler.isPresent()) {
				String label = element.getProperties().getOrDefault(AGILE_ARCHITECTURE_TICKETS_ADR_LABEL, "decision");
				TicketsHandler handler = usableHandler.get();
				writeArchitectureDecisionsUsing(element, ticketsProject, label, handler, builder);
			} else {
				logger.warning(String.format(
						"We have this set of handlers\n%s\nin which we couldn't find one for tickets project %s",
						ticketsHandlers.stream().map(handler -> handler.toString()).collect(Collectors.joining()),
						ticketsProject));
			}
		}
	}
}
