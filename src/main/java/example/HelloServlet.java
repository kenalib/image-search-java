package example;

import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.imagesearch.model.v20180319.SearchItemResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
import static example.ImageSearchDemo.createErrorResponse;

@WebServlet("/search_picture")
@MultipartConfig(maxFileSize = MAX_FILE_SIZE, maxRequestSize = MAX_REQUEST_SIZE)
public class HelloServlet extends HttpServlet {
    private static Properties props;
    private static final Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
    // max size 2MB https://www.alibabacloud.com/help/doc-detail/66610.htm
    static final int MAX_FILE_SIZE = 1024 * 1024 * 2;       // 2MB
    static final int MAX_REQUEST_SIZE = 1024 * 1024 * 8;    // 8MB

    public HelloServlet() throws IOException {
        props = new Properties("image-search.properties");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF8");
        Part part = req.getPart("file_name");
        InputStream inputStream = part.getInputStream();

        if (inputStream.available() == 0) {
            String message = "ERR: input file is empty.";
            System.out.println(message);

            SearchItemResponse response = createErrorResponse(message);
            respondJson(resp, response);
        } else {
            String catId = req.getParameter("cat_id");  // can be null
            System.out.println("INPUT POST catId: " + catId);

            searchPicture(resp, inputStream, catId);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String fileName = "image_search_pictures/bag02.jpg";
        InputStream inputStream = Resources.getInputStream(fileName);

        String catId = req.getParameter("cat_id");  // can be null
        System.out.println("INPUT GET catId: " + catId);

        searchPicture(resp, inputStream, catId);
    }

    private void searchPicture(HttpServletResponse resp, InputStream inputStream, String catId) throws IOException {
        SearchItemResponse response;

        try {
            ImageSearchDemo demo = new ImageSearchDemo();
            response = demo.searchPictureWithResize(inputStream, catId);
        } catch (ClientException e) {
            e.printStackTrace();
            response = createErrorResponse(e.getMessage());
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
