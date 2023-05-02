package org.ndx.aadarchi.base.relationships;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class RelationshipsPropertiesFileReader {

    public static final String DESCRIPTION_PROPERTIES_FILE = "base/src/architecture/resources/relationships-description.properties";

    public Map<String, String> readPropertiesFile() {
        Properties properties = new Properties();
        try (InputStream inputStream = new FileInputStream(DESCRIPTION_PROPERTIES_FILE)){
          properties.load(inputStream);
      } catch (IOException ioException){
          ioException.printStackTrace();
      }
      return getPropertiesFileInput(properties);
    }

    private Map<String, String> getPropertiesFileInput(Properties properties) {
        Map<String, String> relationships = new HashMap<>();
        for (Map.Entry<Object, Object> entry: properties.entrySet()) {
            relationships.put((String) entry.getKey(), (String) entry.getValue());
        }
        return relationships;
    }
}
