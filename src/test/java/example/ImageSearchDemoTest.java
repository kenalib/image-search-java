package example;

import com.aliyuncs.imagesearch.model.v20180319.SearchItemResponse;

import java.util.List;

import static org.junit.Assert.*;

public class ImageSearchDemoTest {
    private ImageSearchDemo demo;

    @org.junit.Before
    public void setUp() {
        demo = new ImageSearchDemo();
    }

    @org.junit.After
    public void tearDown() {
        System.out.println("TEST DONE.");
    }

    @org.junit.Test
    public void testSearchPicture() {
        String fileName = "image_search_pictures/bottle15.jpeg";
        byte[] bytes = Pictures.getBytesFromResource(fileName);

        SearchItemResponse response = demo.searchPicture(bytes);

        assertEquals(true, response.getSuccess());
        assertEquals(0, (int) response.getCode());
        assertEquals("success", response.getMessage());

        SearchItemResponse.PicInfo info = response.getPicInfo();

        assertEquals("8", info.getCategory());

        SearchItemResponse.Head head = response.getHead();
        List<SearchItemResponse.Auction> auctions = response.getAuctions();

        assertEquals(auctions.size(), (int) head.getDocsReturn());
    }

    @org.junit.Test
    public void testSearchPictureEmpty() {
        byte[] bytes = new byte[0];

        SearchItemResponse response = demo.searchPicture(bytes);

        assertNull(response);
    }
}
