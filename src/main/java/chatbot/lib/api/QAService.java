package chatbot.lib.api;

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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ramgathreya on 6/2/17.
 */
public class QAService {
    private static final int timeout = 0;
    private static final String URL = "https://wdaqua-qanary.univ-st-etienne.fr/gerbil-execute/wdaqua-core0,%20QueryExecuter/";

    private HttpClient client;

    public QAService() {
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(this.timeout).build();
        this.client = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
    }

    public String search(String question) throws Exception {
        HttpPost httpPost = new HttpPost(URL);
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("query", question));

        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, Consts.UTF_8);
        httpPost.setEntity(entity);

        HttpResponse response = client.execute(httpPost);

        // Error Scenario
        if(response.getStatusLine().getStatusCode() >= 400) {
            throw new Exception("QANARY Server could not answer due to: " + response.getStatusLine());
        }

        return EntityUtils.toString(response.getEntity());
    }
}
