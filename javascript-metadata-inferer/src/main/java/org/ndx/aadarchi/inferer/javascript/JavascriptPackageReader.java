package org.ndx.aadarchi.inferer.javascript;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.ndx.aadarchi.base.enhancers.scm.SCMHandler;
import org.ndx.aadarchi.inferer.javascript.npm.Dependency;
import org.ndx.aadarchi.inferer.javascript.npm.JavascriptProject;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JavascriptPackageReader {

    @Inject
    Instance<JavascriptPackageAnalyzer> javascriptPackageAnalyzer;

    @Inject
    Instance<SCMHandler> scmHandler;
    @Inject Logger logger;
    
    ObjectMapper mapper = new ObjectMapper()
    		.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);


    public JavascriptProject readNpmProject(FileObject file) {
        try {
            String text = file.getContent().getString("UTF-8");
            JavascriptProject javascriptProject = mapper.readValue(text, JavascriptProject.class);
            return javascriptProject;
        } catch (Exception e) {
            throw new JavascriptDetailsInfererException(String.format("Unable to read package.json file"));
        } finally {
        	try {
				file.close();
			} catch (FileSystemException e) {
				logger.log(Level.SEVERE, String.format("Unable to close file %s", file.getPublicURIString()));
			}
        }
    }

    /*public JavascriptProject readJavascriptProject(String packagePath, URL url) {
        try (InputStream input = SCMHandler.openStream(scmHandler, url)) {
            return readJavascriptProject(packagePath, url);
        } catch (IOException e) {
            throw new JavascriptDetailsInfererException(String.format("Unable to read stream from URL %s", packagePath), e);
        }
        return;
    }*/

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

