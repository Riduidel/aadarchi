package org.ndx.aadarchi.model.linter;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;

public class LoggerHandler extends Handler {
    protected final List<LogRecord> logRecordList = new ArrayList<>();

    @Override
    public void publish(LogRecord record) {
        logRecordList.add(record);
    }

    public List<LogRecord> getLog() {

        return new ArrayList<>(logRecordList);
    }

    public String getSourceMethodName() {
        for (LogRecord logRecord : logRecordList) {
            return logRecord.getSourceMethodName();

        }
        return null;
    }

    public String getLogMessage() {
        for (LogRecord logRecord : logRecordList) {
            return logRecord.getMessage();

        }
        return null;
    }

        @Override
    public void flush() {
    }

    @Override
    public void close() throws SecurityException {
    }

    @Override
    public boolean isLoggable(LogRecord record) {
        return false;
    }
}