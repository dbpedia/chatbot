package chatbot.lib.api.qa;

import chatbot.cache.WolframRepository;
import chatbot.lib.Constants;
import chatbot.lib.Utility;
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
 * Created by ramgathreya on 6/2/17.
 */
public class QAService {
    private static final Logger logger = LoggerFactory.getLogger(QAService.class);

    private QANARY qanary;
    private WolframAlpha wolframAlpha;

    public QAService(WolframRepository wolframRepository) {
        qanary = new QANARY();
        wolframAlpha = new WolframAlpha(wolframRepository);
    }

    // Calls QA Service then returns resulting data as a list of Data Objects. The Data class is defined below as an inner class to be used here locally
    public List<QAService.Data> search(String question) throws Exception {
        List<QAService.Data> datas = qanary.search(question);
        return datas;
    }

    public static class Data {
        public static final String URI = "uri";
        public static final String TYPED_LITERAL = "typed-literal";

        private String type;
        private String value;

        private String processValue(String value) {
            if (Utility.isInteger(value)) {
                return Utility.formatInteger(value);
            }
            return value;
        }

        public String getType() {
            return type;
        }

        public Data setType(String type) {
            this.type = type;
            return this;
        }

        public String getValue() {
            return value;
        }

        public Data setValue(String value) {
            this.value = processValue(value);
            return this;
        }

        public Data(String type, String value) {
            this.type = type;
            this.value = processValue(value);
        }
    }
}
