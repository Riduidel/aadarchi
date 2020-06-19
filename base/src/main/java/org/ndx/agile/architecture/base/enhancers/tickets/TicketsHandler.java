package org.ndx.agile.architecture.base.enhancers.tickets;

import java.util.Collection;

/**
 * Interface implemented by components providing access to project tickets
 * @author nicolas-delsaux
 *
 */
public interface TicketsHandler {

	/** Filtering method
	 * 
	 * @param ticketsProject
	 * @return true if this handler can handle this project
	 */
	boolean canHandle(String ticketsProject);

	Collection<Ticket> getTicketsTagged(String project, String label);

	String getIssuesUrl(String project);

	String getProjectName(String project);
}
