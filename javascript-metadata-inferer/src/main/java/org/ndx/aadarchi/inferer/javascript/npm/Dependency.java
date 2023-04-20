package org.ndx.aadarchi.inferer.javascript.npm;

import java.lang.reflect.Array;

public class Dependency implements java.io.Serializable {
    private String name;
    private String version;
    private Array type;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }
    public Array getType() { return type;}
    public void setType(Array type) { this.type = type; }
}
