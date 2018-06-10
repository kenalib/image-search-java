package example;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class PropertiesTest {
    private Properties props;

    @Before
    public void setUp() throws IOException {
        props = new Properties("image-search.properties");
    }

    @Test
    public void get() {
        assertEquals("ImageSearch", props.get("PRODUCT"));
    }

    @Test
    public void getInt() {
        assertEquals(500, props.getInt("IMAGE_WIDTH"));
    }

    @Test
    public void error() {
        try {
            new Properties("");
            fail("didn't throw IOException");
        } catch (IOException ignored) {
        }
    }
}
