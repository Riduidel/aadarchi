package org.ndx.aadarchi.inferer.javascript;

import com.structurizr.Workspace;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys;

import javax.inject.Inject;
import java.io.File;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
class JavascriptDetailsInfererEnhancerTest {

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
        System.out.println(container);
        // When
        // TODO call the right enhancer
        // Then
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
        // TODO call the right enhancer
        // Then
        Assertions.assertThat(container.getTechnology()).isNotNull();
    }
}