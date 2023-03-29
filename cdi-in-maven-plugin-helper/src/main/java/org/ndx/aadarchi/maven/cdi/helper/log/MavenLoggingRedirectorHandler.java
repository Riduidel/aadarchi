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
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) {
                newSource.append(".");
            }
            newSource.append(parts[i].charAt(0));
        }
        return newSource.toString();
    }

    @Override
    public void publish(LogRecord record) {
        //String source = shortenSource(record.getSourceClassName());
    	Level level = record.getLevel();
		if(level==Level.OFF)
    		return;
        int levelValue = level.intValue();
		if (levelValue <= Level.FINEST.intValue()) {

            mavenLog.debug("<" + record.getSourceClassName() + "> " + record.getMessage(), record.getThrown());
        } else if(levelValue <=Level.FINER.intValue()) {
            mavenLog.debug("<" + record.getSourceClassName() + "> " + record.getMessage(), record.getThrown());
        } else if(levelValue <=Level.FINE.intValue()) {
            mavenLog.debug("<" + record.getSourceClassName() + "> " + record.getMessage(), record.getThrown());
        } else if(levelValue <=Level.CONFIG.intValue()) {
            mavenLog.info("<" + record.getSourceClassName() + "> " + record.getMessage(), record.getThrown());
        } else if(levelValue <=Level.INFO.intValue()) {
            mavenLog.info("<" + record.getSourceClassName() + "> " + record.getMessage(), record.getThrown());
        } else if(levelValue <=Level.WARNING.intValue()) {
            mavenLog.warn("<" + record.getSourceClassName() + "> " + record.getMessage(), record.getThrown());
        } else { //SEVERE
            mavenLog.error("<" + record.getSourceClassName() + "> " + record.getMessage(), record.getThrown());
        }
    }
    // before : [�[1;34mINFO�[m] (org.ndx.aadarchi.base.ArchitectureEnhancer) Running enhancement org.ndx.aadarchi.base.enhancers.scm.SCMProjectCheckouter took 00:00:00.021
    // after  : [�[1;34mINFO�[m] <o.n.a.base.ArchitectureEnhancer> Running enhancement org.ndx.aadarchi.base.enhancers.scm.SCMProjectCheckouter took 00:00:00.021
    @Override
    public void flush() {
    }

    @Override
    public void close() throws SecurityException {
    }
}

