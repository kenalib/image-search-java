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
    private String accessKeyId;
    private String accessKeySecret;
    private String product;
    private String instanceName;
    private String regionId;
    private String endpointName;
    private String domain;
    private String connectTimeout;
    private String readTimeout;
    private int imageWidth;
    private String imageFormatName;
    private IAcsClient client;

    ImageSearchDemo() {
        this.accessKeyId = System.getenv("ACCESS_KEY_ID");
        this.accessKeySecret = System.getenv("ACCESS_KEY_SECRET");

        Properties props = new Properties("image-search.properties");
        this.product = props.get("PRODUCT");
        this.regionId = props.get("REGION_ID");
        this.instanceName = props.get("INSTANCE_NAME");
        this.endpointName = props.get("ENDPOINT_NAME");
        this.domain = props.get("DOMAIN");
        this.connectTimeout = props.get("ConnectTimeout");
        this.readTimeout = props.get("ReadTimeout");
        this.imageWidth = props.getInt("IMAGE_WIDTH");
        this.imageFormatName = props.get("IMAGE_FORMAT_NAME");

        try {
            initClient();
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }

    private void initClient() throws ClientException {
        // set client end time-out
        System.setProperty("sun.net.client.defaultConnectTimeout", connectTimeout);
        System.setProperty("sun.net.client.defaultReadTimeout", readTimeout);

        IClientProfile profile = DefaultProfile.getProfile(
                regionId, accessKeyId, accessKeySecret);

        // add user-defined endpoint
        DefaultProfile.addEndpoint(endpointName, regionId, product, domain);

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

                int originalWidth = bImage.getWidth();
                int originalHeight = bImage.getHeight();

                // minimum size is 200px
                // https://www.alibabacloud.com/help/doc-detail/66610.htm
                if (originalWidth < 200 || originalHeight < 200 || originalWidth > imageWidth) {
                    bImage = ImageUtil.resizeImage(bImage, imageWidth);
                }

                bytes = ImageUtil.bufferedImageToBytes(bImage, imageFormatName);
            }
        } catch (IOException e) {
            e.printStackTrace();
            String message = "ERR: image resize failed.";
            System.out.println(message);

            return createErrorResponse(message);
        }

        return searchPicture(bytes, catId);
    }

    SearchItemResponse searchPicture(byte[] bytes) {
        return searchPicture(bytes, "");
    }

    SearchItemResponse searchPicture(byte[] bytes, String catId) {
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
