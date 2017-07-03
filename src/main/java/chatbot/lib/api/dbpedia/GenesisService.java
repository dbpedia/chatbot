package chatbot.lib.api.dbpedia;

import chatbot.lib.Constants;
import chatbot.lib.response.ResponseData;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ramgathreya on 6/20/17.
 */
public class GenesisService {
    private static final Logger logger = LoggerFactory.getLogger(GenesisService.class);
    private static final String URL = "http://genesis.aksw.org";
    private HttpClient client;

    public GenesisService() {
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(Constants.API_TIMEOUT).build();
        client = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
    }

    private String makeRequest(String endpoint, String uri, String requestType) {
        try {
            HttpPost httpPost = new HttpPost(endpoint);
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("url", uri));

            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, Consts.UTF_8);
            httpPost.setEntity(entity);
            HttpResponse response = client.execute(httpPost);

            String result = null;
            String entities = EntityUtils.toString(response.getEntity());
            JsonNode rootNode = new ObjectMapper().readTree(entities).get(requestType);

            switch (requestType) {
                case "similarEntities":
                case "relatedEntities":
                    int count = 0;
                    result = "";
                    for (JsonNode node : rootNode) {
                        count++;
                        if (count <= ResponseData.MAX_DATA_SIZE) {
                            result += "<" + node.get("url").getTextValue() + "> ";
                        }
                        else {
                            break;
                        }
                    }
                    break;
                case "summary":
                    result = rootNode.getTextValue();
                    break;
            }
            return result.trim();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getSummary(String uri) {
        return makeRequest(URL + "/api/summary", uri, "summary");
    }

    public String getSimilarEntities(String uri) {
         return makeRequest(URL + "/api/similar", uri, "similarEntities");
    }

    public String getRelatedEntities(String uri) {
        return makeRequest(URL + "/api/related", uri, "relatedEntities");
    }
}
