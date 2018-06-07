package example;

import com.aliyuncs.imagesearch.model.v20180319.SearchItemResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

import static example.HelloServlet.MAX_FILE_SIZE;
import static example.HelloServlet.MAX_REQUEST_SIZE;

@WebServlet("/search_picture")
@MultipartConfig(maxFileSize = MAX_FILE_SIZE, maxRequestSize = MAX_REQUEST_SIZE)
public class HelloServlet extends HttpServlet {
    private static final Properties props = new Properties("image-search.properties");
    private static final Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
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

        String catId = req.getParameter("cat_id");  // can be null
        System.out.println("INPUT POST catId: " + catId);

        searchPicture(resp, bytes, catId);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String fileName = "image_search_pictures/bag02.jpg";
        byte[] bytes = Pictures.getBytesFromResource(fileName);

        String catId = req.getParameter("cat_id");  // can be null
        System.out.println("INPUT GET catId: " + catId);

        searchPicture(resp, bytes, catId);
    }

    private void searchPicture(HttpServletResponse resp, byte[] bytes, String catId) throws IOException {
        ImageSearchDemo demo = new ImageSearchDemo();
        SearchItemResponse response = demo.searchPicture(bytes, catId);

        if (response == null) {
            resp.getWriter().write("ERR: response null");
            return;
        }

        respondJson(resp, response);
    }

    private void respondJson(HttpServletResponse resp, SearchItemResponse response) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("utf-8");
        resp.setHeader("Access-Control-Allow-Origin", props.get("CORS_URL"));

        resp.getWriter().write(prettyGson.toJson(
                Collections.singletonMap("SearchItemResponse", response)
        ));
    }
}
