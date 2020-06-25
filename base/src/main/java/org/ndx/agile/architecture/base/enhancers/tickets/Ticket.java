package org.ndx.agile.architecture.base.enhancers.tickets;

import java.util.Collection;
import java.util.Date;
import java.util.Optional;

public interface Ticket {
	String getUrl();

	String getId();

	String getTitle();
	
	/**
	 * @return get ticket text as asciidoc
	 */
	String getText();

	TicketStatus getStatus();
	
	Date getLastDate();
	
	Optional<Date> getDateOf(TicketStatus changeTo);
	
	Collection<Comment> getComments();
}
