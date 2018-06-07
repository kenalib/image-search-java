package example;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

class Pictures {
    static byte[] getBytesFromResource(String fileName) {
        File file = Resources.getFile(fileName);

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
}
