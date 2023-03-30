package org.ndx.aadarchi.maven.cdi.helper.log;

import org.apache.maven.plugin.logging.Log;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import java.util.Arrays;
import java.util.stream.Collectors;

public class MavenLoggingRedirectorHandler extends Handler {

    private Log mavenLog;

    public MavenLoggingRedirectorHandler(Log log) {
        this.mavenLog = log;
    }

    public static String shortenSource(String originalSource) {
        if (originalSource.length() <= 20)
            return (originalSource);
        String[] parts = originalSource.split("\\.");
        String newSource = Arrays.stream(parts, 0, parts.length - 1)
                .map(s -> String.valueOf(s.charAt(0)))
                .collect(Collectors.joining("."));

        newSource += "." + parts[parts.length - 1];
        return newSource.toString();
    }

    @Override
    public void publish(LogRecord record) {
        String source = shortenSource(record.getSourceClassName());
        Level level = record.getLevel();
		if (level == Level.OFF)
    		return;
        int levelValue = level.intValue();

        if (levelValue <= Level.FINEST.intValue()) {
            mavenLog.debug("<" + source + "> " + record.getMessage(), record.getThrown());
        } else if(levelValue <=Level.FINER.intValue()) {
            mavenLog.debug("<" + source + "> " + record.getMessage(), record.getThrown());
        } else if(levelValue <=Level.FINE.intValue()) {
            mavenLog.debug("<" + source + "> " + record.getMessage(), record.getThrown());
        } else if(levelValue <=Level.CONFIG.intValue()) {
            mavenLog.info("<" + source + "> " + record.getMessage(), record.getThrown());
        } else if(levelValue <=Level.INFO.intValue()) {
            mavenLog.info("<" + source + "> " + record.getMessage(), record.getThrown());
        } else if(levelValue <=Level.WARNING.intValue()) {
            mavenLog.warn("<" + source + "> " + record.getMessage(), record.getThrown());
        } else { //SEVERE
            mavenLog.error("<" + source + "> " + record.getMessage(), record.getThrown());
        }
    }
    @Override
    public void flush() {
    }

    @Override
    public void close() throws SecurityException {
    }
}

