package org.ndx.aadarchi.maven.cdi.helper.log;

import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.maven.plugin.logging.Log;

public class MavenLoggingRedirectorHandler extends Handler {

    private Log mavenLog;

    public MavenLoggingRedirectorHandler(Log log) {
        this.mavenLog = log;
    }

    public static Integer getTotalSize(String[] parts) {
        return Arrays.stream(parts)
                .limit(parts.length - 1)
                .mapToInt(String::length)
                .sum() + parts.length - 1;
    }

    public static String shortenSource(String originalSource) {
        String[] parts = originalSource.split("\\.");
        int classLength = parts[parts.length - 1].length();

        for (int i = 0; i < parts.length - 1; i++) {
            if (getTotalSize(parts) + classLength > 20) {
                parts[i] = String.valueOf(parts[i].charAt(0));
            }
        }
        return String.join(".", parts);
    }

    @Override
    public void publish(LogRecord record) {
        Level level = record.getLevel();
		if (level == Level.OFF)
    		return;
        int levelValue = level.intValue();

        if (levelValue <= Level.FINEST.intValue()) {
        	writeLog(mavenLog::debug, record);
        } else if(levelValue <=Level.FINER.intValue()) {
        	writeLog(mavenLog::debug, record);
        } else if(levelValue <=Level.FINE.intValue()) {
        	writeLog(mavenLog::debug, record);
        } else if(levelValue <=Level.CONFIG.intValue()) {
        	writeLog(mavenLog::info, record);
        } else if(levelValue <=Level.INFO.intValue()) {
        	writeLog(mavenLog::info, record);
        } else if(levelValue <=Level.WARNING.intValue()) {
        	writeLog(mavenLog::warn, record);
        } else { //SEVERE
        	writeLog(mavenLog::error, record);
        }
    }
    private void writeLog(BiConsumer<CharSequence, Throwable> logWriter, LogRecord record) {
        String source = shortenSource(record.getSourceClassName());
    	logWriter.accept("<" + source + "> " + record.getMessage(), record.getThrown());
    }
    @Override
    public void flush() {
    }

    @Override
    public void close() throws SecurityException {
    }
}

