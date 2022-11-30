package org.ndx.aadarchi.inferer.javascript;

import com.structurizr.model.Element;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

public class JavascriptProject {

    private String Name;

    private String version;

    private String description;

    private ArrayList<String> keywords;

    private File file;


    private Map<Object, Object> dependencies;

    private Map<String, String> properties;
    private Object modules;

    private JavascriptProject parent;


    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
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

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Map<Object, Object> getDependencies() {
        return dependencies;
    }

    public void setDependencies(Map<Object, Object> dependencies) {
        this.dependencies = dependencies;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void decorate(Element element, JavascriptProject javascriptProject) {
    }
    public Object getModules() {
        return modules;
    }

    public JavascriptProject getParent() {
        return parent;
    }
}
