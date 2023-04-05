package org.ndx.aadarchi.model.linter;

import java.lang.reflect.Method;
import java.util.*;
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
    public void getLogRecordSourceMethodName(String name) {
        if(Objects.equals(name, getMethodName())) {
            for (LogRecord logRecord : logRecordList) {
                logRecord.setSourceClassName(ModelLinter.class.getName());
                logRecord.setSourceMethodName(getMethodName());
                logRecord.getSourceMethodName();
                return;
            }
        }
    }
    private String getMethodName() {
        Class<ModelLinter> sourceClassName = ModelLinter.class;
        Method[] methods = sourceClassName.getMethods();
        for (Method method : methods) {
            return method.getName();
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