package org.ndx.aadarchi.sequence.generator.javaparser;

import java.lang.reflect.Method;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.ndx.aadarchi.sequence.generator.SequenceGeneratorException;

import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedParameterDeclaration;
import com.github.javaparser.resolution.types.ResolvedArrayType;
import com.github.javaparser.resolution.types.ResolvedPrimitiveType;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.resolution.types.ResolvedTypeVariable;

public class Utils {

	private static final int FILENAME_WIDTH = 80;

	/**
	 * From a method name, generates a file name usable for generated sequence diagram
	 * @param method method to generate a file for
	 * @return a string describing that file name
	 */
	public static String methodToFileName(Method method) {
		return StringUtils.abbreviate(String.format("%s_%s%s%s", 
				method.getDeclaringClass().getSimpleName(),
				method.getName(),
				method.getParameters().length>0 ? "___" : "",
				Stream.of(method.getParameters())
					.map(parameter -> parameter.getType().getSimpleName())
					.collect(Collectors.joining("_"))
				), FILENAME_WIDTH);
	}

	public static String methodToFileName(ResolvedMethodDeclaration resolved) {
		return StringUtils.abbreviate(String.format("%s_%s%s%s", 
				resolved.declaringType().getName(),
				resolved.getName(),
				resolved.getNumberOfParams()>0 ? "___" : "",
				methodParametersToFileName(resolved)
				), FILENAME_WIDTH);
	}

	private static String methodParametersToFileName(ResolvedMethodDeclaration resolved) {
		StringBuilder returned = new StringBuilder();
		for (int i = 0; i < resolved.getNumberOfParams(); i++) {
			if(i>0) returned.append("_");
			String fullType = tryToResolveType(resolved.getParam(i).getType());
			if(!fullType.contains("<")) {
				fullType = fullType.substring(fullType.lastIndexOf('.')+1);
			} else {
				fullType = fullType.substring(fullType.lastIndexOf('.', fullType.indexOf('<'))+1);
			}
			returned.append(fullType.replace('.', '_').replace('<', '_').replace('>', '_'));
		}
		return returned.toString();
	}

	public static String toSignature(Method method) {
		return String.format("%s(%s)", method.getName(),
				Stream.of(method.getParameterTypes())
					.map(clazz -> clazz.getName())
					.collect(Collectors.joining(", "))
				);
	}

	public static String toSignature(ResolvedMethodDeclaration method) {
		StringBuilder returned = new StringBuilder(method.getName());
		returned.append('(');
		for (int index = 0; index < method.getNumberOfParams(); index++) {
			ResolvedParameterDeclaration parameter = method.getParam(index);
			if(index>0) {
				returned.append(", ");
			}
			ResolvedType type = parameter.getType();
			returned.append(Utils.tryToResolveType(type));
		}
		returned.append(')');
		return returned.toString();
	}

	public static String tryToResolveType(ResolvedType type) {
		if(type instanceof ResolvedReferenceType) {
			ResolvedReferenceType resolved = (ResolvedReferenceType) type;
			return resolved.getTypeDeclaration().map(resolvedType ->
				resolvedType.getQualifiedName()
			).get();
		} else if(type instanceof ResolvedPrimitiveType) {
			ResolvedPrimitiveType primitiveType = (ResolvedPrimitiveType) type;
			return primitiveType.name();
		} else if(type instanceof ResolvedArrayType) {
			ResolvedArrayType arrayType = (ResolvedArrayType) type;
			return tryToResolveType(arrayType.getComponentType())+"[]";
		} else if(type instanceof ResolvedTypeVariable) {
			ResolvedTypeVariable resolved = (ResolvedTypeVariable) type;
			return resolved.qualifiedName();
		} else {
			throw new SequenceGeneratorException(String.format("We don't know yet how to transform a %s type into a valid signature element."
							+ "Please report a bug at https://github.com/Riduidel/aadarchi-documentation-system/issues",
							type.getClass().getName()));
		}
	}

}
