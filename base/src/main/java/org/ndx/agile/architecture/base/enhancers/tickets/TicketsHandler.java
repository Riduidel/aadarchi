package org.ndx.agile.architecture.base.enhancers.tickets;

import java.util.Collection;

import org.ndx.agile.architecture.base.Configurable;

/**
 * Interface implemented by components providing access to project tickets
 * @author nicolas-delsaux
 *
 */
public interface TicketsHandler extends Configurable {

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
