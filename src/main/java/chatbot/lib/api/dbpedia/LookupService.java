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

import java.net.URLEncoder;

/**
 * Created by ramgathreya on 7/1/17.
 *
 * http://wiki.dbpedia.org/projects/dbpedia-lookup
 */
public class LookupService {
    private static final Logger logger = LoggerFactory.getLogger(LookupService.class);
    private static final String URL = "http://lookup.dbpedia.org/api/search/KeywordSearch";

    private String queryClass = null;
    private int maxHits = 1;

    private HttpClient client;

    public LookupService() {
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(Constants.API_TIMEOUT).build();
        client = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
    }

    // Need to be extended for multiple answers
    public String search(String query) {
        try {
            String url = "?QueryString=" + Utility.urlEncode(query) + "&MaxHits=" + String.valueOf(maxHits);
            if(queryClass != null) {
                url += "&QueryClass=" + String.valueOf(queryClass);
            }

            HttpGet httpGet = new HttpGet(URL + url);
            httpGet.addHeader("Accept", "application/json");
            HttpResponse response = client.execute(httpGet);

            // Error Scenario
            if(response.getStatusLine().getStatusCode() >= 400) {
                logger.error("Lookup Service could not answer due to: " + response.getStatusLine());
                return null;
            }
            else {
                String entities = EntityUtils.toString(response.getEntity());
                JsonNode entity = new ObjectMapper().readTree(entities).get("results").get(0);
                return entity.get("uri").getTextValue();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getQueryClass() {
        return queryClass;
    }

    public LookupService setQueryClass(String queryClass) {
        this.queryClass = queryClass;
        return this;
    }

    public int getMaxHits() {
        return maxHits;
    }

    public LookupService setMaxHits(int maxHits) {
        this.maxHits = maxHits;
        return this;
    }
}
