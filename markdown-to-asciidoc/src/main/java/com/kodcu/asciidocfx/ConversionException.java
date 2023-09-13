package com.kodcu.asciidocfx;

import javax.script.ScriptException;

/**
 * Created by usta on 15.03.2015.
 */
public class ConversionException extends RuntimeException {
    public ConversionException(Exception e) {
        super(e);
    }
}
