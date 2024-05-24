package org.ndx.aadarchi.inferer.maven;

import java.util.Arrays;

import jakarta.inject.Inject;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Test;
import org.ndx.aadarchi.base.ArchitectureEnhancer;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys.ConfigProperties.BasePath;
import org.ndx.aadarchi.cdi.deltaspike.ConfigProperty;

import com.structurizr.Workspace;
import com.structurizr.model.Container;
import com.structurizr.model.SoftwareSystem;

@EnableWeld
public class TestFor435 {
    @WeldSetup
    public WeldInitiator weld = WeldInitiator.performDefaultDiscovery();

    @Inject MavenDetailsInfererEnhancer tested;
	@Inject ArchitectureEnhancer enhancer;
	
	@Inject @ConfigProperty(name=BasePath.NAME, defaultValue = BasePath.VALUE) FileObject basePath;

    @Test public void not_finding_file_doesnt_throw_NullPoinerException() throws FileSystemException {
    	// Given
    	var w = new Workspace(getClass().getName(), "a test workspace");
    	SoftwareSystem system = w.getModel().addSoftwareSystem("The system to decorate with maven informations");
    	system.addProperty(ModelElementKeys.ConfigProperties.BasePath.NAME, 
    			basePath.getName().getPath()+"/a/folder/which/doesnt/exists");
		// When
    	// We emulate in-depth visit (but do not really perform it)
    	enhancer.enhance(w, tested);
		// Then
		Assertions.assertThat(system.getContainers())
			.describedAs("No container should be found, since path in which we search the maven pom doesn't exists")
			.isEmpty()
			;
    }
}
