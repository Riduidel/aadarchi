package org.ndx.aadarchi.inferer.maven.technologies;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

public class VersionDetails {
	private String usages;
	private Date date;
	private long users;
	@JsonCreator
	public VersionDetails(
			@JsonProperty("usages") String usages,
			@JsonFormat(locale = "en-US", 
				pattern = "MMM dd, yyyy",
				shape = Shape.STRING)
			@JsonProperty("date") Date date,
			@JsonProperty("users") long users) {
		this.usages = usages;
		this.date = date;
		this.users = users;
	}
}
