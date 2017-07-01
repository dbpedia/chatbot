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

import java.util.*;

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
    public Data search(String question) throws Exception {
        return qanary.search(question).addData(wolframAlpha.search(question));
    }

    public static class Data {
        private HashSet<String> uris = new HashSet<>();
        private HashSet<String> literals = new HashSet<>();

        private String processValue(String value) {
            if (Utility.isInteger(value)) {
                return Utility.formatInteger(value);
            }
            return value;
        }

        public Data addURI(String uri) {
            this.uris.add(uri);
            return this;
        }

        public Data addLiteral(String literal) {
            this.literals.add(processValue(literal));
            return this;
        }

        public Data addData(Data data) {
            uris.addAll(data.getUris());
            literals.addAll(data.getLiterals());
            return this;
        }

        public HashSet<String> getUris() {
            return uris;
        }

        public HashSet<String> getLiterals() {
            return literals;
        }
    }
}
