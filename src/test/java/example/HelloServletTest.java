package example;

import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.imagesearch.model.v20180319.SearchItemResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HelloServletTest {
    private StringWriter sw;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);

        sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        when(response.getWriter()).thenReturn(pw);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void doPost() throws IOException, ServletException {
        ImageSearchDemo mock = setupImageSearchMock();
        HelloServlet helloServlet = new HelloServlet(mock);

        when(request.getParameter("cat_id")).thenReturn("");

        Part part = new DummyFile("image_search_pictures/bag02.jpg");
        when(request.getPart("file_name")).thenReturn(part);

        helloServlet.doPost(request, response);
        String result = sw.getBuffer().toString().trim();

        Gson g = new Gson();
        JsonObject obj = g.fromJson(result, JsonObject.class);
        SearchItemResponse res = g.fromJson(
                obj.get("SearchItemResponse"), SearchItemResponse.class);

        assertTrue(res.getSuccess());
        assertEquals("success", res.getMessage());
        assertEquals("DUMMY", res.getRequestId());
    }

    @Test
    public void doPostEmptyFile() throws IOException, ServletException, ClientException {
        HelloServlet helloServlet = new HelloServlet();

        Part part = new DummyFile("");
        when(request.getPart("file_name")).thenReturn(part);

        helloServlet.doPost(request, response);
        String result = sw.getBuffer().toString().trim();

        Gson g = new Gson();
        JsonObject obj = g.fromJson(result, JsonObject.class);
        SearchItemResponse res = g.fromJson(
                obj.get("SearchItemResponse"), SearchItemResponse.class);

        assertFalse(res.getSuccess());
        assertEquals("ERR: input file is empty.", res.getMessage());
    }

    @Test
    public void doGetStartWithoutMock() throws IOException, ClientException {
        new HelloServlet();
    }

    @Test
    public void doGet() throws IOException {
        ImageSearchDemo mock = setupImageSearchMock();
        HelloServlet helloServlet = new HelloServlet(mock);

        when(request.getParameter("cat_id")).thenReturn("");

        helloServlet.doGet(request, response);
        String result = sw.getBuffer().toString().trim();

        Gson g = new Gson();
        JsonObject obj = g.fromJson(result, JsonObject.class);
        SearchItemResponse res = g.fromJson(
                obj.get("SearchItemResponse"), SearchItemResponse.class);

        assertTrue(res.getSuccess());
        assertEquals("success", res.getMessage());
        assertEquals("DUMMY", res.getRequestId());
    }

    private ImageSearchDemo setupImageSearchMock() {
        ImageSearchDemo mock = mock(ImageSearchDemo.class);

        SearchItemResponse imageRes = createDummyResponse();

        when(
                mock.searchPictureWithResize(any(InputStream.class), anyString())
        ).thenReturn(imageRes);

        return mock;
    }

    private SearchItemResponse createDummyResponse() {
        SearchItemResponse response = new SearchItemResponse();

        response.setCode(0);
        response.setMessage("success");
        response.setRequestId("DUMMY");
        response.setSuccess(true);

        SearchItemResponse.Auction a1 = new SearchItemResponse.Auction();
        List<SearchItemResponse.Auction> auctions = new ArrayList<>();
        auctions.add(a1);

        response.setAuctions(auctions);

        return response;
    }
}
