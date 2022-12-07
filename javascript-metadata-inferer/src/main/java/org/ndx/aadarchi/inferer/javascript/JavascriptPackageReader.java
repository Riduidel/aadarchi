package org.ndx.aadarchi.inferer.javascript;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ndx.aadarchi.base.enhancers.scm.SCMHandler;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Paths;

public class JavascriptPackageReader {

    @Inject
    Instance<JavascriptPackageAnalyzer> javascriptPackageAnalyzer;

    @Inject
    Instance<SCMHandler> scmHandler;

    public JavascriptProject readNpmProject(String packagePath) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JavascriptProject javascriptProject = mapper.readValue(Paths.get("src/test/package.json").toFile(), JavascriptProject.class);
            javascriptProject.getProperties().put(JavascriptDetailsInfererEnhancer.NPM_PACKAGE_URL, packagePath);
            javascriptProject.getProperties().put(JavascriptDetailsInfererEnhancer.NPM_MODULE_DIR, packagePath.substring(0, packagePath.lastIndexOf('/') + 1));
        } catch (Exception e) {
            throw new JavascriptDetailsInfererException(String.format("Unable to read package.json file"));
        }
        return null;
    }

    public JavascriptProject readJavascriptProject(String packagePath, URL url) {
        try (InputStream input = SCMHandler.openStream(scmHandler, url)) {
            return readJavascriptProject(packagePath, url);
        } catch (IOException e) {
            throw new JavascriptDetailsInfererException(String.format("Unable to read stream from URL %s", packagePath), e);
        }
    }

    public Dependency readDependencies() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Dependency dependency = mapper.readValue(Paths.get("src/test/package.json").toFile(), Dependency.class);
        } catch (IOException e) {
            throw new JavascriptDetailsInfererException(String.format("Unable to read dependencies from package.json"), e);

        }
        return null;
    }
}

