package org.ndx.aadarchi.github.vfs;

import static org.junit.jupiter.api.Assertions.fail;

import javax.inject.Inject;

import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Test;

@EnableWeld
class GitHubFileSystemProviderTest {
	@WeldSetup
	public WeldInitiator weld = WeldInitiator.performDefaultDiscovery();
	
	@Inject GitHubFileSystemProvider gitHubFileSystem;

	@Test
	void test() {
		
		fail("Not yet implemented");
	}

}
