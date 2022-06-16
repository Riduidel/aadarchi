package org.ndx.aadarchi.sequence.generator.javaparser;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.ndx.aadarchi.base.enhancers.ModelElementKeys;
import org.ndx.aadarchi.sequence.generator.SequenceGeneratorException;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.github.javaparser.symbolsolver.utils.SymbolSolverCollectionStrategy;
import com.github.javaparser.utils.ProjectRoot;
import com.pivovarit.function.ThrowingFunction;
import com.structurizr.model.Container;

public class ProjectRootBuilder {
	private static final Logger logger = Logger.getLogger(ProjectRootBuilder.class.getName());
	private List<String> files;

	public ProjectRootBuilder(Map<String, Container> pathsToContainers) {
		this.files = new ArrayList<>(pathsToContainers.keySet());
	}

	public ProjectRoot build(Container container) {
		ProjectRoot projectRoot = null;
		if(files.isEmpty()) {
			throw new SequenceGeneratorException(String.format("Unable to parse source for %s, as it is linked to no source path", container));
		}
		List<Path> paths = files.stream()
				.map(ThrowingFunction.unchecked(file -> ModelElementKeys.fileAsUrltoPath(file)))
				.collect(Collectors.toList());
		
		Path initialPath = paths.get(0);
		CombinedTypeSolver typeSolver = new CombinedTypeSolver(
					new JavaParserTypeSolver(initialPath)
//					new ReflectionTypeSolver()
				);
		for(Path p : paths) {
			typeSolver.add(new JavaParserTypeSolver(p));
		}
		typeSolver.add(new ReflectionTypeSolver());
		projectRoot = new SymbolSolverCollectionStrategy(
				new ParserConfiguration()
				.setSymbolResolver(new JavaSymbolSolver(typeSolver))
				).collect(initialPath);
		// Now we have a big map of paths as string. Let's inject them
		for(int index=1; index<paths.size(); index++) {
			projectRoot.addSourceRoot(paths.get(index));
		}
		return projectRoot;
	}

}
