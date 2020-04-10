package org.ndx.agile.architecture.github.readme;


import java.io.IOException;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ReaderTest {
	@Test void a_token_is_defined() {
		Assertions.assertThat(System.getProperty("GITHUB_TOKEN"))
			.as("For these tests to run, you have to define a GITHUB_TOKEN system property")
			.isNotNull();
	}

	@Test void can_read_an_accessible_project() throws IOException {
		ReadmeReader reader = new ReadmeReader();
		String readme = reader.getReadmeContent(System.getProperty("GITHUB_TOKEN"), 
				"Riduidel/agile-architecture-documentation-archetype", 
				"README.md");
		Assertions.assertThat(readme).isNotEmpty();
	}

	@Test void can_read_an_accessible_project_with_full_project_name() throws IOException {
		ReadmeReader reader = new ReadmeReader();
		String readme = reader.getReadmeContent(System.getProperty("GITHUB_TOKEN"), 
				"https://github.com/Riduidel/agile-architecture-documentation-archetype", 
				"README.md");
		Assertions.assertThat(readme).isNotEmpty();
	}

	@Test void can_read_an_accessible_project_with_no_readme_given() throws IOException {
		ReadmeReader reader = new ReadmeReader();
		String readme = reader.getReadmeContent(System.getProperty("GITHUB_TOKEN"), 
				"https://github.com/Riduidel/agile-architecture-documentation-archetype", 
				null);
		Assertions.assertThat(readme).isNotEmpty();
	}

	@Test() void can_not_read_an_inaccessible_project() throws IOException {
		ReadmeReader reader = new ReadmeReader();
		org.junit.jupiter.api.Assertions.assertThrows(IOException.class, () -> {
		String readme = reader.getReadmeContent(System.getProperty("GITHUB_TOKEN"), 
				"https://github.com/notauser/notaproject", 
				"README.md");
		});
	}


	@Test() void can_read_my_old_snowcamp_project() throws IOException {
		ReadmeReader reader = new ReadmeReader();
		String readme = reader.getReadmeContent(System.getProperty("GITHUB_TOKEN"), 
				"https://github.com/Riduidel/snowcamp-2019", 
				null);
		Assertions.assertThat(readme).isNotEmpty();
	}
}
