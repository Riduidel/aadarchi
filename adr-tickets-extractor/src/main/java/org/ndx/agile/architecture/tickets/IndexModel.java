package org.ndx.agile.architecture.tickets;

import java.util.Map;

import org.ndx.agile.architecture.base.enhancers.tickets.Ticket;

/**
 * Data model for the index page of issues list
 * @author nicolas-delsaux
 *
 */
public class IndexModel {

	private final String projectUrl;
	private final String projectName;
	private final String label;
	private final Map<Ticket, String> ticketsTexts;

	public IndexModel(String projectUrl, String projectName, String label, Map<Ticket, String> ticketsTexts) {
		this.projectUrl = projectUrl;
		this.projectName = projectName;
		this.label = label;
		this.ticketsTexts = ticketsTexts;
	}

	public String getProjectUrl() {
		return projectUrl;
	}

	public String getLabel() {
		return label;
	}

	public String getProjectName() {
		return projectName;
	}

	public Map<Ticket, String> getTicketsTexts() {
		return ticketsTexts;
	}

}
