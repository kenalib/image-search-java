package example;

import com.aliyuncs.imagesearch.model.v20180319.SearchItemResponse;
import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import static example.HelloServlet.MAX_FILE_SIZE;
import static example.HelloServlet.MAX_REQUEST_SIZE;

@WebServlet("/search_picture")
@MultipartConfig(maxFileSize = MAX_FILE_SIZE, maxRequestSize = MAX_REQUEST_SIZE)
public class HelloServlet extends HttpServlet {
    private static final Gson gson = new Gson();
    static final int MAX_FILE_SIZE = 1024 * 1024 * 2;       // 2MB
    static final int MAX_REQUEST_SIZE = 1024 * 1024 * 8;    // 8MB

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF8");
        Part part = req.getPart("file_name");

        InputStream inputStream = part.getInputStream();
        byte[] bytes = IOUtils.toByteArray(inputStream);

        if (bytes.length == 0) {
            resp.getWriter().write("ERROR: input file error");
            return;
        }

        searchPicture(resp, bytes);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String fileName = "image_search_pictures/bottle15.jpeg";
        byte[] bytes = Pictures.getBytesFromResource(fileName);

        searchPicture(resp, bytes);
    }

    private void searchPicture(HttpServletResponse resp, byte[] bytes) throws IOException {
        ImageSearchDemo demo = new ImageSearchDemo();
        SearchItemResponse response = demo.searchPicture(bytes);

        if (response == null) {
            resp.getWriter().write("ERR: response null");
            return;
        }

        List<SearchItemResponse.Auction> auctions = response.getAuctions();
        respondAuctionJson(resp, auctions);
    }

    private void respondAuctionJson(HttpServletResponse resp, List<SearchItemResponse.Auction> auctions) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("utf-8");
        resp.setHeader("Access-Control-Allow-Origin", "http://image-search-demo2.oss-ap-southeast-1.aliyuncs.com");

        resp.getWriter().write(gson.toJson(
                Collections.singletonMap("Auctions", auctions)
        ));
    }
}
