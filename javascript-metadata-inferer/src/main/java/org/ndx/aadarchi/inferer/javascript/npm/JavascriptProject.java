package org.ndx.aadarchi.inferer.javascript.npm;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class JavascriptProject implements Cloneable {
    /**
     * name field is the identifier that is completely unique
     */
    @JsonProperty("name")
    private String name;
    /**
     * version field is the second identifier that is completely unique
     */
    @JsonProperty("version")
    private String version;
    private String description;
    private ArrayList<String> keywords;
    private String homepage;
    private File file;
    private String bugs;
    private String license;
    /**
     * dictionary containing script commands that are run at
     * various times in the lifecycle of your package
     */
    private Map<Object, Object> scripts;

    /**
     * Dependencies used in production
     */
    @JsonProperty("dependencies")
    @JsonAlias({"devDependencies", "peerDependencies", "peerDependenciesMeta", "bundleDependencies","optionalDependencies"})
    public Map<Object, Object> dependencies;
    /**
     * version of node or npm that we work
     */
    private Map<Object, Object> engines;
    /**
     * the operating system which the module run on
     */
    
    private Set os;
    private Map<String, String> properties;
    private Object modules;

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

    public Map<Object, Object> getScripts() {
        return scripts;
    }

    public void setScripts(Map<Object, Object> scripts) {
        this.scripts = scripts;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(ArrayList<String> keywords) {
        this.keywords = keywords;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getBugs() {
        return bugs;
    }

    public void setBugs(String bugs) {
        this.bugs = bugs;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public Map<Object, Object> getDependencies() {
        return dependencies;
    }
    public void setDependencies(Map<Object, Object> dependencies) {
        this.dependencies = dependencies;
    }
    public Map<Object, Object> getEngines() {
        return engines;
    }

    public void setEngines(Map<Object, Object> engines) {
        this.engines = engines;
    }

    public Set getOs() {
        return os;
    }

    public void setOs(Set os) {
        this.os = os;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public Object getModules() {
        return modules;
    }

    public void setModules(Object modules) {
        this.modules = modules;
    }
}
