package chatbot.lib.api.dbpedia;

import chatbot.lib.Constants;
import chatbot.lib.Utility;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by ramgathreya on 7/1/17.
 */
public class SpotlightService {
    private static final Logger logger = LoggerFactory.getLogger(SpotlightService.class);
    private static final String URL = "http://model.dbpedia-spotlight.org/en/annotate";

    private double confidence = 0.7;

    private HttpClient client;

    public SpotlightService() {
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(Constants.API_TIMEOUT).build();
        client = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
    }

    public String search(String query) {
        try {
            String url = "?text=" + Utility.urlEncode(query) + "&confidence=" + confidence;
            HttpGet httpGet = new HttpGet(URL + url);
            httpGet.addHeader("Accept", "application/json");
            HttpResponse response = client.execute(httpGet);

            // Error Scenario
            if(response.getStatusLine().getStatusCode() >= 400) {
                logger.error("Spotlight Service could not answer due to: " + response.getStatusLine());
                return null;
            }
            else {
                String entities = EntityUtils.toString(response.getEntity());
                JsonNode entity = new ObjectMapper().readTree(entities).get("Resources").get(0);
                return entity.get("@URI").getTextValue();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
