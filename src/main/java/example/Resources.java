package example;

import java.io.File;
import java.net.URL;
import java.util.Objects;

class Resources {
    static File getFile(String resourceFileName) {
        ClassLoader classLoader = Pictures.class.getClassLoader();
        URL url = classLoader.getResource(resourceFileName);
        String filePath = Objects.requireNonNull(url).getFile();

        return new File(filePath);
    }
}
