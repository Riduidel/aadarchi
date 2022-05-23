package org.ndx.agile.architecture.documentation.system.maven.cdi.helper.log;

import org.apache.maven.plugin.logging.Log;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class MavenLoggingRedirectorHandler extends Handler {

    private Log mavenLog;

    public MavenLoggingRedirectorHandler(Log log) {
        this.mavenLog = log;
    }

    @Override
    public void publish(LogRecord record) {
    	Level level = record.getLevel();
		if(level==Level.OFF)
    		return;
        int levelValue = level.intValue();
		if (levelValue <= Level.FINEST.intValue()) {
            mavenLog.debug("(" + record.getSourceClassName() + ") " + record.getMessage(), record.getThrown());
        } else if(levelValue <=Level.FINER.intValue()) {
            mavenLog.debug("(" + record.getSourceClassName() + ") " + record.getMessage(), record.getThrown());
        } else if(levelValue <=Level.FINE.intValue()) {
            mavenLog.debug("(" + record.getSourceClassName() + ") " + record.getMessage(), record.getThrown());
        } else if(levelValue <=Level.CONFIG.intValue()) {
            mavenLog.info("(" + record.getSourceClassName() + ") " + record.getMessage(), record.getThrown());
        } else if(levelValue <=Level.INFO.intValue()) {
            mavenLog.info("(" + record.getSourceClassName() + ") " + record.getMessage(), record.getThrown());
        } else if(levelValue <=Level.WARNING.intValue()) {
            mavenLog.warn("(" + record.getSourceClassName() + ") " + record.getMessage(), record.getThrown());
        } else { //SEVERE
            mavenLog.error("(" + record.getSourceClassName() + ") " + record.getMessage(), record.getThrown());
        }
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() throws SecurityException {
    }
}

