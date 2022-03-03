package nl.jworks.markdown_to_asciidoc;

import static org.junit.Assert.assertEquals;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class Stepdefs {

    private String markdown;
    private String asciiDoc;

    @Given("^the Markdown source$")
    public void the_markdown_source(String markdown) throws Throwable {
        if (markdown.contains("{sp}")) {
            this.markdown = markdown.replaceAll("\\{sp\\}", " ");
        }
        else {
            this.markdown = markdown;
        }
    }

    @When("^it is converted to AsciiDoc")
    public void it_is_converted_to_asciidoc() throws Throwable {
        // Express the Regexp above with the code you wish you had
        this.asciiDoc = Converter.convertMarkdownToAsciiDoc(markdown);
    }

    @Then("^the result should match the AsciiDoc source$")
    public void the_result_should_match_the_asciidoc_source(String result) throws Throwable {
        // Express the Regexp above with the code you wish you had

        assertEquals(result, asciiDoc);

    }
}
