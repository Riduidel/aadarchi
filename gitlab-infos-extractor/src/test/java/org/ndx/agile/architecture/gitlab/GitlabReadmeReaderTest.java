package org.ndx.agile.architecture.gitlab;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GitlabReadmeReaderTest {
	@Test void a_token_is_defined() {
		Assertions.assertThat(System.getProperty("GITLAB_TOKEN"))
			.as("For these tests to run, you have to define a GITLAB_TOKEN system property")
			.isNotNull();
	}

	static class AccessProjectsTest {
		private GitlabReadmeReader reader;
		@BeforeEach public void initializeApi() {
			reader = new GitlabReadmeReader();
			reader.createGitlabApi(System.getProperty("GITLAB_TOKEN"));
		}
		@Test void can_read_an_accessible_project() {
			String readme = reader.getReadmeContent("tlcampbell23/SumApp", 
					"README.md");
			Assertions.assertThat(readme).isNotEmpty();
		}
		@Test void can_read_an_accessible_project_with_no_readme_specified() {
			String readme = reader.getReadmeContent("tlcampbell23/SumApp", 
					"README.md");
			Assertions.assertThat(readme).isNotEmpty();
		}
	}
}
