package example;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ResourcesTest {
    private String fileName;

    @Before
    public void setUp() {
        fileName = "image_search_pictures/bag02.jpg";
    }

    @Test
    public void getInputStream() throws IOException {
        InputStream inputStream = Resources.getInputStream(fileName);
        assertTrue(inputStream.available() > 0);
    }

    @Test
    public void getBytes() {
        byte[] bytes = Resources.getBytes(fileName);
        assertTrue(bytes.length > 0);

        bytes = Resources.getBytes("");
        assertEquals(0, bytes.length);
    }
}
