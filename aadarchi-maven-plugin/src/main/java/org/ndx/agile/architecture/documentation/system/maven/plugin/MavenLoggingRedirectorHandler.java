package org.ndx.agile.architecture.documentation.system.maven.plugin;

import org.apache.maven.plugin.logging.Log;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class MavenLoggingRedirectorHandler extends Handler {

    private Log mavenLog;

    public MavenLoggingRedirectorHandler(Log log) {
        this.mavenLog = log;
    }

    @Override
    public void publish(LogRecord record) {
        if(record.getLevel().intValue() < 300) {             //ALL
            mavenLog.info("(" + record.getSourceClassName() + ") " + record.getMessage());
        } else if (record.getLevel().intValue() == 300) {    //FINEST
            mavenLog.debug("(" + record.getSourceClassName() + ") " + record.getMessage());
        } else if(record.getLevel().intValue() == 400) {     //FINER
            mavenLog.debug("(" + record.getSourceClassName() + ") " + record.getMessage());
        } else if(record.getLevel().intValue() == 500) {     //FINE
            mavenLog.debug("(" + record.getSourceClassName() + ") " + record.getMessage());
        } else if(record.getLevel().intValue() == 700) {     //CONFIG
            mavenLog.info("(" + record.getSourceClassName() + ") " + record.getMessage());
        } else if(record.getLevel().intValue() == 800) {     //INFO
            mavenLog.info("(" + record.getSourceClassName() + ") " + record.getMessage());
        } else if(record.getLevel().intValue() == 900) {     //WARNING
            mavenLog.warn("(" + record.getSourceClassName() + ") " + record.getMessage());
        } else if(record.getLevel().intValue() == 1000) {    //SEVERE
            mavenLog.error("(" + record.getSourceClassName() + ") " + record.getMessage());
        } else {                                             //OFF
            mavenLog.error("(" + record.getSourceClassName() + ") " + record.getMessage());
        }
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() throws SecurityException {
    }
}

