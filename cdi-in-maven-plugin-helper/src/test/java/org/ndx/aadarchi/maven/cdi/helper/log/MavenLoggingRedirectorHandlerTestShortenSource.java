package org.ndx.aadarchi.maven.cdi.helper.log;

import static org.junit.jupiter.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class MavenLoggingRedirectorHandlerTestShortenSource {
	@Test public void do_not_shorten_short_class_name() {
		// Given
		String className = String.class.getName();
		// When
		String shortened = MavenLoggingRedirectorHandler.shortenSource(className);
		// Then
		Assertions.assertThat(shortened).isEqualTo(className);
	}
	@Test public void partially_shorten_medium_class_name() {
		// Given
		String className = "1.22.333.4444.55555.Do";
		// When
		String shortened = MavenLoggingRedirectorHandler.shortenSource(className);
		// Then
		Assertions.assertThat(shortened).isEqualTo("1.2.3.4444.55555.Do");
	}
	@Test public void shorten_long_class_name() {
		// Given
		String className = "org.ndx.aadarchi.gitlab.GitlabSCMHandler";
		// When
		String shortened = MavenLoggingRedirectorHandler.shortenSource(className);
		// Then
		Assertions.assertThat(shortened).isEqualTo("o.n.a.g.GitlabSCMHandler");
	}
}
