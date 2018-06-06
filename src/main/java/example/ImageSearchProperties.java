package example;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

class ImageSearchProperties {
    static String getInstanceName() {
        String propertiesFile = "image-search.properties";
        String instanceName = "";
        Properties properties = new Properties();

        try {
            InputStream is = new FileInputStream(propertiesFile);
            properties.load(is);
            instanceName = properties.getProperty("INSTANCE_NAME");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return instanceName;
    }
}
