package org.ndx.aadarchi.inferer.javascript;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys;

import javax.inject.Inject;
import java.io.File;
import java.nio.file.Paths;

public class JavascriptPackageReader {

    @Inject
    @ConfigProperty(name= ModelElementKeys.ConfigProperties.BasePath.NAME, defaultValue = ModelElementKeys.ConfigProperties.BasePath.VALUE)
    File basePath;

    @Inject
    JavascriptPackageAnalyzer javascriptPackageAnalyzer;

    public JavascriptProject readNpmProject(String packagePath) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JavascriptProject javascriptProject = mapper.readValue(Paths.get("package.json").toFile(), JavascriptProject.class);
            javascriptProject.getProperties().put(JavascriptDetailsInfererEnhancer.NPM_PACKAGE_URL, packagePath);
            javascriptProject.getProperties().put(JavascriptDetailsInfererEnhancer.NPM_MODULE_DIR, packagePath.substring(0, packagePath.lastIndexOf('/') + 1));

        } catch (Exception e) {
            throw  new JavascriptDetailsInfererException(String.format("Unable to read package.json file"));
        }
            return null;
    }
}
