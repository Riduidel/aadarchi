package org.ndx.aadarchi.inferer.maven;

import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Named;

import org.ndx.aadarchi.base.AgileArchitectureSection;
import org.ndx.aadarchi.base.OutputBuilder;
import org.ndx.aadarchi.base.enhancers.ModelElementAdapter;
import org.ndx.aadarchi.inferer.maven.technologies.MvnRepositoryArtifact;
import org.ndx.aadarchi.inferer.maven.technologies.MvnRepositoryArtifactsProducer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.structurizr.model.StaticStructureElement;

/**
 * An enhancer which generates in development environment some text about the
 * notable dependencies
 */
@Default @ApplicationScoped
public class MavenTechnologiesDocumentationEnhancer extends ModelElementAdapter {
	@Inject @Named(MvnRepositoryArtifactsProducer.MVNREPOSITORY_ARTIFACTS) Map<String, MvnRepositoryArtifact> mvnRepositoryArtifacts;
	ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public int priority() {
		return 10;
	}

	@Override
	protected void processElement(StaticStructureElement element, OutputBuilder builder) {
		if(element.getProperties().containsKey(MavenEnhancer.AGILE_ARCHITECTURE_MAVEN_TECHNOLOGIES)) {
			String technologies = element.getProperties().get(MavenEnhancer.AGILE_ARCHITECTURE_MAVEN_TECHNOLOGIES);
			// Rehydrate that to have artifacts
			try {
				Map<String, String> dependenciesVersions = objectMapper.readValue(technologies, MavenEnhancer.AGILE_ARCHITECTURE_MAVEN_TECHNOLOGIES_TYPE);
				if(!dependenciesVersions.isEmpty()) {
					writeDependenciesArtifacts(element, dependenciesVersions, builder);
				}
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private Comparator<Map.Entry<MvnRepositoryArtifact, String>> comparator() {
		return new Comparator<Map.Entry<MvnRepositoryArtifact, String>>() {

			@Override
			public int compare(Entry<MvnRepositoryArtifact, String> o1, Entry<MvnRepositoryArtifact, String> o2) {
				return o1.getKey().compareTo(o2.getKey());
			}
			
		};
	}

	private void writeDependenciesArtifacts(StaticStructureElement element, Map<String, String> dependenciesVersions,
			OutputBuilder builder) {
		String text = dependenciesVersions.entrySet().stream()
			.map(entry -> Map.entry(mvnRepositoryArtifacts.get(entry.getKey()), entry.getValue()))
			.sorted(this.comparator())
			.map(entry -> this.toTableRow(entry.getKey(), entry.getValue()))
			.collect(Collectors.joining("\n", 
					"[%autowidth.stretch, cols=\"1a,1a,1a,1a\"]\n|==="
					+ "\n|Name|Used version|Categories|Description\n\n", 
					"\n|==="));
		builder.writeToOutput(AgileArchitectureSection.development_environment, element, this, OutputBuilder.Format.adoc, 
				"# Interesting dependencies\n\n" +
				text);
	}

	private String toTableRow(MvnRepositoryArtifact key, String version) {
		StringBuilder returned = new StringBuilder()
			.append("|").append(key.page).append("[").append(key.name).append("] ");
		returned
			.append("|").append(version==null || version.isBlank() ? "{nbsp}" : version);
		if(key.versions.containsKey(version))
			returned.append(" (released ").append(key.versions.get(version)).append(")");
		returned.append("|");
		if(key.categories==null || key.categories.isEmpty()) {
			returned.append("{nbsp}");
		} else {
			returned.append(key.categoriesText);
		}
		returned
			.append("|").append(key.description)
			;
		return returned.toString();
	}
}
