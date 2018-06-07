package example;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

class Properties {
    private java.util.Properties properties;

    Properties(String fileName) {
        properties = new java.util.Properties();
        File file = Resources.getFile(fileName);

        try {
            InputStream is = new FileInputStream(file);
            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    String get(String key) {
        return properties.getProperty(key);
    }

//    // for integer property
//    int getInt(String key) {
//        String value = properties.getProperty(key);
//        return Integer.parseInt(value);
//    }
}
