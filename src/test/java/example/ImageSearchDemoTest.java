package example;

import com.aliyuncs.imagesearch.model.v20180319.SearchItemResponse;

import java.util.List;

import static org.junit.Assert.*;

public class ImageSearchDemoTest {
    private ImageSearchDemo demo;
    private String fileName;
    private String searchCategory;
    private String detectedCategory;
    private int auctionSize;

    @org.junit.Before
    public void setUp() {
        demo = new ImageSearchDemo();
    }

    @org.junit.After
    public void tearDown() {
        System.out.println("TEST DONE.");
    }

    @org.junit.Test
    public void testSearch() {
        // category 3 picture return category 3 as expected
        fileName = "image_search_pictures/bag02.jpg";
        searchCategory = "";
        detectedCategory = "3";
        auctionSize = 5;
        searchPicture(fileName, searchCategory, detectedCategory, auctionSize);
    }

    @org.junit.Test
    public void testSearchWithCategory() {
        fileName = "image_search_pictures/bag01.jpg";

        // category 3 picture but detected as 9 so return 0 result
        searchCategory = "";
        detectedCategory = "9";
        auctionSize = 0;
        searchPicture(fileName, searchCategory, detectedCategory, auctionSize);

        // category 3 picture with category 3 return 5 as expected
        searchCategory = "3";
        detectedCategory = "3";
        auctionSize = 5;
        searchPicture(fileName, searchCategory, detectedCategory, auctionSize);
    }

    private void searchPicture(String fileName, String searchCat, String detectedCat, int auctionSize) {
        byte[] bytes = Pictures.getBytesFromResource(fileName);

        SearchItemResponse response = demo.searchPicture(bytes, searchCat);

        assertEquals(true, response.getSuccess());
        assertEquals(0, (int) response.getCode());
        assertEquals("success", response.getMessage());

        SearchItemResponse.PicInfo info = response.getPicInfo();

        assertEquals(detectedCat, info.getCategory());

        SearchItemResponse.Head head = response.getHead();
        List<SearchItemResponse.Auction> auctions = response.getAuctions();

        assertEquals(auctionSize, auctions.size());
        assertEquals(auctions.size(), (int) head.getDocsReturn());
    }

    @org.junit.Test
    public void testSearchPictureSmall() {
        fileName = "image_search_pictures/bag05.jpg";
        byte[] bytes = Pictures.getBytesFromResource(fileName);

        SearchItemResponse response = demo.searchPicture(bytes);

        assertEquals(response.getSuccess(), false);
        assertTrue(response.getMessage().startsWith("UnsupportedPicPixels"));
    }

    @org.junit.Test
    public void testSearchPictureEmpty() {
        byte[] bytes = new byte[0];

        SearchItemResponse response = demo.searchPicture(bytes);

        assertEquals(response.getSuccess(), false);
        assertEquals(response.getMessage(), "request.buildPostContent() failed.");
    }
}
