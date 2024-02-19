package org.ndx.aadarchi.freemarker;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.assertj.core.api.Assertions;
import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Test;

import freemarker.template.Template;

@EnableWeld
public class FreemarkerCDIProducerTest {

	@WeldSetup
	public WeldInitiator weld = WeldInitiator.performDefaultDiscovery();
	
	@Inject Template template;
	
//	@Test
	public void can_inject_freemarker_template() {
		Assertions.assertThat(template).isNotNull();
	}
}