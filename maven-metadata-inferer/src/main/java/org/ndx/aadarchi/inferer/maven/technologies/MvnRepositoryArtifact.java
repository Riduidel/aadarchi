package org.ndx.aadarchi.inferer.maven.technologies;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MvnRepositoryArtifact {
	public final String name;
	public final String coordinates;
	public String description;
	public List<String> categories;
	public List<String> tags;
	public Map<String, String> versions;
	public String page;

	@JsonCreator
	public MvnRepositoryArtifact(@JsonProperty("name") String name, 
			@JsonProperty("coordinates") String coordinates,
			@JsonProperty("description") String description,
			@JsonProperty("page") String page,
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
		this.tags = tags;
		this.versions = versions;
	}
}
