package example;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

class Pictures {
    static byte[] getBytesFromResource(String fileName) {
        File file = getFileFromResource(fileName);

        return getBytesFromFile(file);
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

    private static File getFileFromResource(String fileName) {
        ClassLoader classLoader = Pictures.class.getClassLoader();
        String filePath = Objects.requireNonNull(classLoader.getResource(fileName)).getFile();

        return new File(filePath);
    }
}
