package org.ndx.aadarchi.maven.cdi.helper.log;

import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.apache.maven.plugin.logging.SystemStreamLog;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MavenLoggingRedirectorHandlerTest {

	private Logger logger;
	private SystemStreamLog mavenLog;
	private Map<CharSequence, Throwable> messages = new TreeMap<>();

	// Create an appender that stores logs in an array, which make them readable later in assertions
	@BeforeEach public void collectLogsInArray() {
		mavenLog = new SystemStreamLog() {
			@Override
			public void info(CharSequence content, Throwable error) {
				messages.put(content, error);
			}
		};
		logger = Logger.getLogger("test");
		logger.addHandler(new MavenLoggingRedirectorHandler(mavenLog));
	}

	@Test
	void test() {
		// Given
		final String MESSAGE = "An info message";
		// When
		logger.info(MESSAGE);
		// Then
		String expected = String.format("<%s> %s",
				// Already tested in MavenLoggingRedirectorHandlerTestShortenSource
				MavenLoggingRedirectorHandler.shortenSource(getClass().getName()),
				MESSAGE);
		Assertions.assertThat(messages).containsKey(expected);
	}

}
