package org.ndx.aadarchi.base.relationships;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class RelationshipsPropertiesFileReader {


    public Map<String, String> readPropertiesFile(String filePath) {
        Properties properties = new Properties();
        try (InputStream inputStream = new FileInputStream(filePath)){
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
