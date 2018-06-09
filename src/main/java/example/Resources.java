package example;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;

class Resources {
    static InputStream getInputStream(String fileName) throws FileNotFoundException {
        File file = getFile(fileName);
        return new FileInputStream(file);
    }

    static byte[] getBytes(String fileName) {
        File file = getFile(fileName);

        return getBytesFromFile(file);
    }

    private static File getFile(String resourceFileName) {
        ClassLoader classLoader = Resources.class.getClassLoader();
        URL url = classLoader.getResource(resourceFileName);
        String filePath = Objects.requireNonNull(url).getFile();

        return new File(filePath);
    }

    private static byte[] getBytesFromFile(File file) {
        byte[] bytes = new byte[0];

        try {
            bytes = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
            return bytes;
        }

        return bytes;
    }
}
