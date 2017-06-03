package chatbot.lib.handlers;

import chatbot.lib.api.QAService;
import chatbot.lib.api.SPARQL;
import chatbot.lib.response.ResponseData;
import chatbot.lib.response.ResponseType;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by ramgathreya on 6/2/17.
 */
public class NLHandler {
    private static final Logger logger = LoggerFactory.getLogger(NLHandler.class);

    private String question;
    private QAService qaService;
    private SPARQL sparql;

    public NLHandler(String question) {
        this.question = question;
        this.qaService = new QAService();
        this.sparql = new SPARQL();
    }

    public List<ResponseData> answer() throws Exception {
        List<Data> data = this.invokeQAService(qaService.search(question));
        return processResponseData(data);
    }

    private List<ResponseData> processResponseData(List<Data> data) {
        List<ResponseData> responseData = null;
        if(data != null && data.size() > 0) {
            responseData = new ArrayList<>();
            for(Data item : data) {
                switch(item.getType()) {
                    case Data.URI:
                        ResponseData _data = sparql.setSelect("DISTINCT ?property ?value")
                                .setWhere("<" + item.getValue() + "> ?property ?value. filter( (?property = rdfs:label && lang(?value) = 'en' ) || (?property = dbo:abstract && lang(?value) = 'en' ) || ?property=dbo:thumbnail || ?property=foaf:isPrimaryTopicOf) .")
                                .executeQuery();
                        if (_data != null) {
                            // Adding button to view in DBpedia
                            _data.addButton(new ResponseData.ButtonData("View in DBpedia", ResponseType.BUTTON_LINK, item.getValue()));
                            responseData.add(_data);
                        }
                        break;
                }
            }
        }
        return responseData;
    }

    // Calls QA Service then returns resulting data as a list of Data Objects. The Data class is defined below as an inner class to be used here locally
    private List<Data> invokeQAService(String response) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(response);
        JsonNode answers = mapper.readTree(rootNode.findValue("questions").get(0).get("question").get("answers").getTextValue());

        if (answers != null) {
            JsonNode bindings = answers.get("results").get("bindings");
            List<Data> data = new ArrayList<>();
            //            JsonNode binding = bindings.get(0);
            for(JsonNode binding : bindings) {
                Iterator<Map.Entry<String, JsonNode>> nodes = binding.getFields();
                while (nodes.hasNext()) {
                    Map.Entry<String, JsonNode> entry = nodes.next();
                    JsonNode value = entry.getValue();
                    data.add(new Data(value.get("type").getTextValue(), value.get("value").getTextValue()));
                }
            }
            return data;
        }
        return null;
    }

    class Data {
        private static final String URI = "uri";

        private String type;
        private String value;

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
            this.value = value;
            return this;
        }

        public Data(String type, String value) {
            this.type = type;
            this.value = value;
        }
    }
}
