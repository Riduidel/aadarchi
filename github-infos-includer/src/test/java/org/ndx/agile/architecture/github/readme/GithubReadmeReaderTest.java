package org.ndx.agile.architecture.github.readme;


import java.io.IOException;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GithubReadmeReaderTest {
	@Test void a_token_is_defined() {
		Assertions.assertThat(System.getProperty("GITHUB_TOKEN"))
			.as("For these tests to run, you have to define a GITHUB_TOKEN system property")
			.isNotNull();
	}
	
	static class CanReadProjectsTest {
		
		private GitHubReadmeReader reader;

		@BeforeEach public void loadReader() {
			reader = new GitHubReadmeReader();
			reader.createGithubApi(System.getProperty("GITHUB_TOKEN"));
		}

		@Test void can_read_an_accessible_project() {
			String readme = reader.getReadmeContent("Riduidel/agile-architecture-documentation-archetype", 
					"README.md");
			Assertions.assertThat(readme).isNotEmpty();
		}

		@Test void can_read_an_accessible_project_with_full_project_name() {
			String readme = reader.getReadmeContent("https://github.com/Riduidel/agile-architecture-documentation-archetype", 
					"README.md");
			Assertions.assertThat(readme).isNotEmpty();
		}

		@Test void can_read_an_accessible_project_with_no_readme_given() {
			String readme = reader.getReadmeContent("https://github.com/Riduidel/agile-architecture-documentation-archetype", 
					null);
			Assertions.assertThat(readme).isNotEmpty();
		}

		@Test() void can_not_read_an_inaccessible_project() {
			org.junit.jupiter.api.Assertions.assertThrows(CantExtractReadme.class, () -> {
			String readme = reader.getReadmeContent("https://github.com/notauser/notaproject", 
					"README.md");
			});
		}


		@Test() void can_read_my_old_snowcamp_project() {
			String readme = reader.getReadmeContent("https://github.com/Riduidel/snowcamp-2019", 
					null);
			Assertions.assertThat(readme).isNotEmpty();
		}
	}
}
