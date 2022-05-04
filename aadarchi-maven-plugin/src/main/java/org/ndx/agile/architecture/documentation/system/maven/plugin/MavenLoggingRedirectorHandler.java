package org.ndx.agile.architecture.documentation.system.maven.plugin;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

import org.apache.maven.plugin.logging.Log;

public class MavenLoggingRedirectorHandler extends Handler {

    private Log mavenLog;

    public MavenLoggingRedirectorHandler(Log log) {
        this.mavenLog = log;
    }

    @Override
    public void publish(LogRecord record) {
        mavenLog.info(record.getMessage());
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() throws SecurityException {
    }
}

