package example;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import static org.junit.Assert.*;

public class ImageUtilTest {
    private BufferedImage bImage;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    @Before
    public void setUp() throws Exception {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

        String fileName = "image_search_pictures/bag02.jpg";
        InputStream is = Resources.getInputStream(fileName);
        bImage = ImageIO.read(is);
    }

    @After
    public void tearDown() {
        System.setOut(System.out);
        System.setErr(System.err);
    }

    @Test
    public void main() throws IOException {
        String[] args = {""};
        ImageUtil.main(args);
        assertEquals("Usage: ImageUtil input_file output_file\n", outContent.toString());
    }

    @Test
    public void main2() {
        String[] args = {"a", "b"};
        try {
            ImageUtil.main(args);
        } catch (IOException ignored) {
        }
        assertEquals("resizing a to b\n", outContent.toString());
    }

    @Test
    public void resizeImage() {
        bImage = ImageUtil.resizeImage(bImage, 500);
        assertEquals(500, bImage.getWidth());
    }

    @Test
    public void bufferedImageToBytes() throws IOException {
        byte[] bytes = ImageUtil.bufferedImageToBytes(bImage, "jpeg");
        assertTrue(bytes.length > 0);
    }
}
