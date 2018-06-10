package example;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.imagesearch.model.v20180319.SearchItemRequest;
import com.aliyuncs.imagesearch.model.v20180319.SearchItemResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

class ImageSearchDemo {
    private Properties props;
    private IAcsClient client;
    private String accessKeyId ;
    private String accessKeySecret;

    ImageSearchDemo() throws IOException, ClientException {
        this(new Env());
    }

    ImageSearchDemo(Env env) throws ClientException, IOException {
        accessKeyId = env.get("ACCESS_KEY_ID");
        accessKeySecret = env.get("ACCESS_KEY_SECRET");
        this.props = new Properties("image-search.properties");

        // set client end time-out
        System.setProperty("sun.net.client.defaultConnectTimeout", props.get("ConnectTimeout"));
        System.setProperty("sun.net.client.defaultReadTimeout", props.get("ReadTimeout"));

        IClientProfile profile = DefaultProfile.getProfile(
                props.get("REGION_ID"), accessKeyId, accessKeySecret
        );

        // add user-defined endpoint
        DefaultProfile.addEndpoint(
                props.get("ENDPOINT_NAME"),
                props.get("REGION_ID"),
                props.get("PRODUCT"),
                props.get("DOMAIN")
        );

        client = new DefaultAcsClient(profile);
    }

    SearchItemResponse searchPictureWithResize(InputStream inputStream) {
        return searchPictureWithResize(inputStream, "");
    }

    SearchItemResponse searchPictureWithResize(InputStream inputStream, String catId) {
        byte[] bytes;

        try {
            if (inputStream.available() == 0) {
                bytes = new byte[0];
            } else {
                BufferedImage bImage = ImageIO.read(inputStream);

                if (bImage == null) {
                    throw new IOException("ERR: ImageIO.read null");
                }

                int originalWidth = bImage.getWidth();
                int originalHeight = bImage.getHeight();

                int imageWidth = props.getInt("IMAGE_WIDTH");
                String imageFormatName = props.get("IMAGE_FORMAT_NAME");

                // minimum size is 200px
                // https://www.alibabacloud.com/help/doc-detail/66610.htm
                if (originalWidth < 200 || originalHeight < 200 || originalWidth > imageWidth) {
                    bImage = ImageUtil.resizeImage(bImage, imageWidth);
                }

                bytes = ImageUtil.bufferedImageToBytes(bImage, imageFormatName);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return createErrorResponse(e.getMessage());
        }

        return searchPicture(bytes, catId);
    }

    SearchItemResponse searchPicture(byte[] bytes) {
        return searchPicture(bytes, "");
    }

    SearchItemResponse searchPicture(byte[] bytes, String catId) {
        // note: if accessKeyId is null, client will throw NullPointerException
        if (accessKeyId == null || accessKeySecret == null) {
            String message = "ACCESS_KEY_ID ACCESS_KEY_SECRET null";
            return createErrorResponse(message);
        }

        SearchItemRequest request = createSearchItemRequest(catId);
        SearchItemResponse response;

        request.setSearchPicture(bytes);

        if (!request.buildPostContent()) {
            String message = "request.buildPostContent() failed.";
            System.out.println(message);

            return createErrorResponse(message);
        }

        try {
            response = client.getAcsResponse(request);
            printResponse(response);
        } catch (ClientException e) {
            // [possible error] com.aliyuncs.exceptions.ClientException:
            // UnsupportedPicPixels : Unsupported picture pixels.
            e.printStackTrace();

            response = createErrorResponse(e.getMessage());
            response.setRequestId(e.getRequestId());
        }

        return response;
    }

    private SearchItemRequest createSearchItemRequest(String catId) {
        SearchItemRequest request = new SearchItemRequest();
        String instanceName = props.get("INSTANCE_NAME");

        request.setInstanceName(instanceName);
        request.setNum(12);
        request.setStart(0);

        // same logic as in SearchItemRequest.buildPostContent()
        if (catId != null && catId.length() > 0) {
            System.out.println("CATEGORY SET: " + catId);
            request.setCatId(catId);
        } else {
            System.out.println("CATEGORY NOT SET");
        }

        return request;
    }

    static SearchItemResponse createErrorResponse(String message) {
        SearchItemResponse response = new SearchItemResponse();
        response.setSuccess(false);
        response.setMessage(message);

        return response;
    }

    private void printResponse(SearchItemResponse response) {
        System.out.println("RequestId" + response.getRequestId());
        System.out.println("Success: " + response.getSuccess());
        System.out.println("Code: " + response.getCode());
        System.out.println("Message: " + response.getMessage());

        SearchItemResponse.Head head = response.getHead();

        System.out.println("Head:DocsFound: " + head.getDocsFound());
        System.out.println("Head:DocsReturn: " + head.getDocsReturn());
        System.out.println("Head:SearchTime: " + head.getSearchTime());

        SearchItemResponse.PicInfo info = response.getPicInfo();

        System.out.println("PicInfo:Region: " + info.getRegion());
        System.out.println("PicInfo:Category: " + info.getCategory());
        System.out.println("PicInfo:AllCategory.size(): " + info.getAllCategory().size());

        List<SearchItemResponse.Auction> auctions = response.getAuctions();

        System.out.println("Auctions.size(): " + auctions.size());
    }
}
