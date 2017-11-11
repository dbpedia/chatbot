package chatbot.lib.api.qa;

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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by ramgathreya on 7/1/17.
 */
public class QANARY {
    private static final Logger logger = LoggerFactory.getLogger(QANARY.class);
    private static final String URL = "https://wdaqua-qanary.univ-st-etienne.fr/gerbil-execute/wdaqua-core1,%20QueryExecuter/";

    private HttpClient client;

    public QANARY() {
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(Constants.API_TIMEOUT).build();
        this.client = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
    }

    private String makeRequest(String question) {
        try {
            HttpPost httpPost = new HttpPost(URL);
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("query", question));

            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, Consts.UTF_8);
            httpPost.setEntity(entity);

            HttpResponse response = client.execute(httpPost);

            // Error Scenario
            if(response.getStatusLine().getStatusCode() >= 400) {
                logger.error("QANARY Server could not answer due to: " + response.getStatusLine());
                return null;
            }

            return EntityUtils.toString(response.getEntity());
        }
        catch(Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    // Calls QANARY Service then returns resulting data as a list of Data Objects
    public QAService.Data search(String question) throws Exception {
        QAService.Data data = new QAService.Data();
        String response = makeRequest(question);
        if(response != null) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response);
            JsonNode answers = mapper.readTree(rootNode.findValue("questions").get(0).get("question").get("answers").getTextValue());

            if (answers != null) {
                JsonNode bindings = answers.get("results").get("bindings");
                for(JsonNode binding : bindings) {
                    Iterator<Map.Entry<String, JsonNode>> nodes = binding.getFields();
                    while (nodes.hasNext()) {
                        Map.Entry<String, JsonNode> entry = nodes.next();
                        JsonNode value = entry.getValue();
                        switch(value.get("type").getTextValue()) {
                            case "uri":
                                data.addURI(value.get("value").getTextValue());
                                break;
                            case "typed-literal":
                                data.addLiteral(value.get("value").getTextValue());
                                break;
                        }
                    }
                }
            }
        }
        return data;
    }

}
