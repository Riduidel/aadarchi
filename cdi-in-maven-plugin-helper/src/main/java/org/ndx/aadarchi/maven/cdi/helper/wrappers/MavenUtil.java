package org.ndx.aadarchi.maven.cdi.helper.wrappers;

import java.io.File;
import java.util.List;
import java.util.Optional;

import org.apache.maven.model.Dependency;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.impl.ArtifactResolver;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;

/**
 * A utility class for maven related stuff such as resolving of dependencies,
 * ...
 *
 * @author <a href="mailto:stanley.hillner@itemis.de">Stanley Hillner</a>
 * @since 2.0.0
 */
public class MavenUtil {
	/**
	 * Uses the aether to resolve a plugin dependency and returns the file for
	 * further processing.
	 *
	 * @param d                 the dependency to resolve.
	 * @param pluginRepos       the plugin repositories to use for dependency
	 *                          resolution.
	 * @param resolver          the resolver for aether access.
	 * @param repoSystemSession the session for the resolver.
	 * @return optionally a file which is the resolved dependency.
	 */
	public static Optional<File> resolveDependency(Dependency d, List<RemoteRepository> pluginRepos,
			ArtifactResolver resolver, RepositorySystemSession repoSystemSession) {
		Artifact a = new DefaultArtifact(d.getGroupId(), d.getArtifactId(), d.getClassifier(), d.getType(),
				d.getVersion());
		ArtifactRequest artifactRequest = new ArtifactRequest();
		artifactRequest.setArtifact(a);
		artifactRequest.setRepositories(pluginRepos);
		try {
			ArtifactResult artifactResult = resolver.resolveArtifact(repoSystemSession, artifactRequest);
			Artifact resolvedArtifact = artifactResult.getArtifact();
			if (resolvedArtifact != null) {
				return Optional.ofNullable(resolvedArtifact.getFile());
			}
			return Optional.empty();
		} catch (ArtifactResolutionException e) {
			return Optional.empty();
		}
	}
}
