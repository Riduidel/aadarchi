package org.ndx.aadarchi.base.relationships;

import org.assertj.core.api.Assertions;
import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.Map;

@EnableWeld
public class RelationshipsPropertiesFileReaderTest {

    @WeldSetup
    public WeldInitiator weld = WeldInitiator.performDefaultDiscovery();

    @Inject
    RelationshipsPropertiesFileReader relationshipsPropertiesFileReader;
    public static final String FILE_PATH = "src/test/architecture/resources/relationship-description-test.properties";

    private Map<String, String> getPropertiesFileInput() {
        return relationshipsPropertiesFileReader.readPropertiesFile(FILE_PATH);
    }

   @Test
    public void inputContainer1_centerContainer1_relationship_key_should_return_the_right_value() {
        //Given
       Map<String, String> propertiesFileInput = getPropertiesFileInput();
       //When
        String value = propertiesFileInput.get("inputContainer1->centerContainer1");
        //Then
        Assertions.assertThat(value).isEqualTo("relationship between inputContainer1 and centerContainer1");
    }

    @Test
    public void inputContainer2_centerContainer1_relationship_key_should_return_the_right_value() {
        //Given
        Map<String, String> propertiesFileInput = relationshipsPropertiesFileReader.readPropertiesFile(FILE_PATH);
        //When
        String value = propertiesFileInput.get("inputContainer2->centerContainer1");
        //Then
        Assertions.assertThat(value).isEqualTo("relationship between inputContainer2 and centerContainer1");
    }

    @Test
    public void centerContainer1_outputContainer1_relationship_key_should_return_the_right_value() {
        //Given
        Map<String, String> propertiesFileInput = relationshipsPropertiesFileReader.readPropertiesFile(FILE_PATH);
        //When
        String value = propertiesFileInput.get("centerContainer1->outputContainer1");
        //Then
        Assertions.assertThat(value).isEqualTo("relationship between centerContainer1 and outputContainer1");
    }

    @Test
    public void centerContainer1_outputContainer2_relationship_key_should_return_the_right_value() {
        //Given
        Map<String, String> propertiesFileInput = relationshipsPropertiesFileReader.readPropertiesFile(FILE_PATH);
        //When
        String value = propertiesFileInput.get("centerContainer1->outputContainer2");
        //Then
        Assertions.assertThat(value).isEqualTo("relationship between centerContainer1 and outputContainer2");
    }
}