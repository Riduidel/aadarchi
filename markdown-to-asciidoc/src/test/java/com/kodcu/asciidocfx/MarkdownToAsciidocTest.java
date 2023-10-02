package com.kodcu.asciidocfx;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class MarkdownToAsciidocTest {
	@Test
	void can_convert_a_text() {
		Assertions.assertThat(MarkdownToAsciidoc.convert("text")).isEqualTo("\ntext\n");
	}
	@Test
	void can_run_multiple_parallel_calls_for_388() {
		// Given
		// When
		String text = IntStream.range(0, 100).parallel()
			.mapToObj(index -> MarkdownToAsciidoc.convert("text "+index))
			.collect(Collectors.joining("\n"));
		// Then
		Assertions.assertThat(text).containsSubsequence("text 1", "text 2", "text 3");
	}

}
