package org.ndx.aadarchi.maven.cdi.helper.log;

import java.util.Arrays;
import java.util.logging.Logger;

import org.apache.maven.plugin.logging.Log;

public class BindJULToMaven {
    public static void accept(Log log) {
        Logger root = Logger.getLogger("");
        Arrays.stream(root.getHandlers()).forEach(root::removeHandler);
        root.addHandler(new MavenLoggingRedirectorHandler(log));
    }
}
