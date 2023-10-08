package org.ndx.aadarchi.inferer.maven.technologies;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MvnRepositoryArtifact {
	public final String name;
	public final String coordinates;
	public final String description;
	public final List<String> categories;
	public final List<String> tags;
	public final Map<String, String> versions;
	public final String page;
	public final int ranking;
	public final int users;

	@JsonCreator
	public MvnRepositoryArtifact(@JsonProperty("name") String name, 
			@JsonProperty("coordinates") String coordinates,
			@JsonProperty("description") String description,
			@JsonProperty("page") String page,
			@JsonProperty("ranking") int ranking,
			@JsonProperty("users") int users,
			@JsonProperty("categories") List<String> categories,
			@JsonProperty("tags") List<String> tags,
			@JsonProperty("versions") Map<String, String> versions
			) {
		super();
		this.name = name;
		this.coordinates = coordinates;
		this.page = page;
		this.description = description;
		this.categories = categories;
		this.users = users;
		this.ranking = ranking;
		this.tags = tags;
		this.versions = versions;
	}
}
