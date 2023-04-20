package org.ndx.aadarchi.inferer.javascript;

import java.io.File;
import java.util.Arrays;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ndx.aadarchi.base.ArchitectureEnhancer;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys;

import com.structurizr.Workspace;

@EnableWeld
class JavascriptDetailsInfererEnhancerTest {
    @WeldSetup
    public WeldInitiator weld = WeldInitiator.performDefaultDiscovery();
    @Inject JavascriptDetailsInfererEnhancer tested;
	@Inject ArchitectureEnhancer enhancer;

    //call processModelElement to find package.json
    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    public void ensure_package_json_is_loaded_from_one_folder() throws Exception {
        // Given
        Workspace w = new Workspace("test", "ensure we can read package.json");
        var system = w.getModel().addSoftwareSystem("testSystem");
        var container = system.addContainer("packageJsonTest");
        File file = new File("src/test/qvgdc-app/package.json");
        Assertions.assertThat(file)
        	.isFile();
        container.addProperty(JavascriptEnhancer.AGILE_ARCHITECTURE_NPM_PACKAGE, file.toURL().toString());
                //TODO write path of qvgdc as a string
        // When
    	// We emulate in-depth visit (but do not really perform it)
    	enhancer.enhance(w, Arrays.asList(tested));
        // Then
        Assertions.assertThat(container.getTechnology())
                .describedAs("A javascript project should have technology loaded from the package.json file")
                .isNotEmpty();
    }


    @Test
    public void ensure_package_json_is_loaded_from_github() {
        // Given
        Workspace w = new Workspace("test", "ensure we can read package.json");
        var system = w.getModel().addSoftwareSystem("testSystem");
        var container = system.addContainer("packageJsonTest");
        container.addProperty(ModelElementKeys.Scm.PROJECT, "https://github.com/Zenika/qvgdc-app");
        // When
    	// We emulate in-depth visit (but do not really perform it)
    	enhancer.enhance(w, Arrays.asList(tested));
        // Then
        Assertions.assertThat(container.getTechnology()).isNotEmpty();
    }
}