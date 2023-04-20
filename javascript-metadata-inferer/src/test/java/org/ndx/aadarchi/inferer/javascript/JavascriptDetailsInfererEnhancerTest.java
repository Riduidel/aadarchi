package org.ndx.aadarchi.inferer.javascript;

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
    public void ensure_package_json_is_loaded_from_one_folder() {
        // Given
        Workspace w = new Workspace("test", "ensure we can read package.json");
        var system = w.getModel().addSoftwareSystem("testSystem");
        var container = system.addContainer("packageJsonTest");
        container.addProperty(ModelElementKeys.ConfigProperties.BasePath.NAME, "src/test/qvgdc-app");
                //TODO write path of qvgdc as a string
<<<<<<< Updated upstream
=======
        System.out.println("bahahahahaha");
        System.out.println(container.getTechnology());
>>>>>>> Stashed changes
        // When
    	// We emulate in-depth visit (but do not really perform it)
    	enhancer.enhance(w, Arrays.asList(tested));
        // Then
        Assertions.assertThat(container.getTechnology()).isNotEmpty();
        Assertions.assertThat(container.getTechnology()).isNotNull();
    }


    @Test
    public void ensure_package_json_is_loaded_from_github() {
        // Given
        Workspace w = new Workspace("test", "ensure we can read package.json");
        var system = w.getModel().addSoftwareSystem("testSystem");
        var container = system.addContainer("packageJsonTest");
        container.addProperty(ModelElementKeys.Scm.PROJECT, "https://github.com/Zenika/qvgdc-app");
                // TODO get gqvdc scm url
        // When
    	// We emulate in-depth visit (but do not really perform it)
    	enhancer.enhance(w, Arrays.asList(tested));
        // Then
        System.out.println(container.getTechnology());
        Assertions.assertThat(container.getTechnology()).isNotNull();
    }
}