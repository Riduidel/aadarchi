package org.ndx.aadarchi.maven.cdi.helper.log;

import org.apache.maven.plugin.logging.Log;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class MavenLoggingRedirectorHandler extends Handler {

    private Log mavenLog;

    public MavenLoggingRedirectorHandler(Log log) {
        this.mavenLog = log;
    }

    public static String shortenSource(String originalSource) {
        String[] parts = originalSource.split("\\.");
        StringBuilder newSource = new StringBuilder();
        for (int i = 0; i < parts.length - 1; i++) {
            if (i > 0) {
                newSource.append(".");
            }
            newSource.append(parts[i].charAt(0));
        }
        newSource.append("." + parts[parts.length - 1]);
        return newSource.toString();
    }

    @Override
    public void publish(LogRecord record) {
        String source = record.getSourceClassName().length() <= 20 ? record.getSourceClassName() : shortenSource(record.getSourceClassName());
        Level level = record.getLevel();
		if(level==Level.OFF)
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

