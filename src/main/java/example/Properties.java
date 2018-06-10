package example;

import java.io.IOException;
import java.io.InputStream;

class Properties {
    private java.util.Properties properties;

    Properties(String fileName) throws IOException {
        InputStream is = Resources.getInputStream(fileName);

        properties = new java.util.Properties();
        properties.load(is);
    }

    String get(String key) {
        return properties.getProperty(key);
    }

    int getInt(String key) {
        String value = properties.getProperty(key);
        return Integer.parseInt(value);
    }
}
