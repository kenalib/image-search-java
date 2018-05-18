package example;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.imagesearch.model.v20180319.SearchItemRequest;
import com.aliyuncs.imagesearch.model.v20180319.SearchItemResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

import java.util.List;

class ImageSearchDemo {
    private String accessKeyId;
    private String accessKeySecret;
    private String instanceName;
    private String regionId;
    private String endpointName;
    private String domain;
    private IAcsClient client;

    ImageSearchDemo() {
        this.accessKeyId = System.getenv("ACCESS_KEY_ID");
        this.accessKeySecret = System.getenv("ACCESS_KEY_SECRET");
        this.instanceName = System.getenv("INSTANCE_NAME");
        this.regionId = System.getenv("REGION_ID");   // e.g. "ap-southeast-1";
        this.endpointName = getenv("ENDPOINT_NAME", "ap-southeast-1");
        this.domain = getenv("DOMAIN", "imagesearch.ap-southeast-1.aliyuncs.com");

        try {
            initClient();
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }

    private void initClient() throws ClientException {
        // set client end time-out
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");

        IClientProfile profile = DefaultProfile.getProfile(
                regionId, accessKeyId, accessKeySecret);

        // add user-defined endpoint
        DefaultProfile.addEndpoint(endpointName, regionId, "ImageSearch", domain);

        client = new DefaultAcsClient(profile);
    }

    SearchItemResponse searchPicture(byte[] bytes) {
        SearchItemRequest request = createSearchItemRequest();

        request.setSearchPicture(bytes);

        if (!request.buildPostContent()) {
            System.out.println("build post content failed.");
            return null;
        }

        SearchItemResponse response;

        try {
            response = client.getAcsResponse(request);
            printResponse(response);
        } catch (ClientException e) {
            e.printStackTrace();
            return null;
        }

        return response;
    }

    private SearchItemRequest createSearchItemRequest() {
        SearchItemRequest request = new SearchItemRequest();

        request.setInstanceName(instanceName);
        request.setNum(12);
        request.setStart(0);
        request.setCatId("8");

        return request;
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

    private String getenv(String name, String def) {
        String env = System.getenv(name);
        return (env == null) ? def : env;
    }
}