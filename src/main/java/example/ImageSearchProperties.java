package example;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

class ImageSearchProperties {
    static String getInstanceName() {
        String propertiesFile = "image-search.properties";
        String instanceName = "";
        Properties properties = new Properties();

        try {
            File file = getFileFromResource(propertiesFile);
            InputStream is = new FileInputStream(file);
            properties.load(is);
            instanceName = properties.getProperty("INSTANCE_NAME");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return instanceName;
    }

    private static File getFileFromResource(String fileName) {
        ClassLoader classLoader = Pictures.class.getClassLoader();
        String filePath = Objects.requireNonNull(classLoader.getResource(fileName)).getFile();

        return new File(filePath);
    }
}
