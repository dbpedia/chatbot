package chatbot.lib.handlers;

import chatbot.lib.Utility;
import chatbot.lib.api.QAService;
import chatbot.lib.api.SPARQL;
import chatbot.lib.request.Request;
import chatbot.lib.response.Response;
import chatbot.lib.response.ResponseData;
import chatbot.lib.response.ResponseGenerator;
import chatbot.lib.response.ResponseType;
import chatbot.rivescript.RiveScriptBot;
import chatbot.rivescript.RiveScriptReplyType;
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

    private static final int MAX_DATA_SIZE = 10;

    private String question;
    private QAService qaService;
    private SPARQL sparql;

    private Request request;
    private RiveScriptBot riveScriptBot;

    public NLHandler(Request request, String question, RiveScriptBot riveScriptBot) {
        this.question = question;
        this.qaService = new QAService();
        this.sparql = new SPARQL();

        this.request = request;
        this.riveScriptBot = riveScriptBot;
    }

    public List<Response> answer() throws Exception {
        ResponseGenerator responseGenerator = new ResponseGenerator();
        List<Data> data = this.invokeQAService(qaService.search(question));
        ProcessedResponse processedResponse = processResponseData(data);
        List<ResponseData> responseDatas = processedResponse.getResponseData();

        switch(processedResponse.getResponseType()) {
            case ProcessedResponse.RESPONSE_TEXT:
                responseGenerator.addTextResponse(responseDatas.get(0));
                break;
            case ProcessedResponse.RESPONSE_CAROUSEL:
                responseGenerator.addTextResponse(new ResponseData(riveScriptBot.answer(request.getUserId(), RiveScriptReplyType.NL_ANSWER_TEXT)[0]));
                responseGenerator.addCarouselResponse(responseDatas.toArray(new ResponseData[responseDatas.size()]));
                break;
            default:
                responseGenerator.addTextResponse(new ResponseData(riveScriptBot.answer(request.getUserId(), RiveScriptReplyType.FALLBACK_TEXT)[0]));
        }
        return responseGenerator.getResponse();
    }

    private ProcessedResponse processResponseData(List<Data> data) {
        ProcessedResponse processedResponse = new ProcessedResponse();
        if(data != null && data.size() > 0) {
            for(Data item : data) {
                switch(item.getType()) {
                    case Data.TYPED_LITERAL:
                        processedResponse.setResponseType(ProcessedResponse.RESPONSE_TEXT);
                        processedResponse.setResponseData(new ArrayList<ResponseData>() {{
                            add(new ResponseData(item.getValue()));
                        }});
                        return processedResponse;
                    case Data.URI:
                        processedResponse.setResponseType(ProcessedResponse.RESPONSE_CAROUSEL);
                        ResponseData _data = sparql.getEntityInformation(item.getValue());
                        if (_data != null) {
                            processedResponse.addResponseData(_data);
                        }
                        break;
                }
            }
        }
        return processedResponse;
    }

    // Calls QA Service then returns resulting data as a list of Data Objects. The Data class is defined below as an inner class to be used here locally
    private List<Data> invokeQAService(String response) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(response);
        JsonNode answers = mapper.readTree(rootNode.findValue("questions").get(0).get("question").get("answers").getTextValue());

        if (answers != null) {
            JsonNode bindings = answers.get("results").get("bindings");
            List<Data> data = new ArrayList<>();

            for(JsonNode binding : bindings) {
                Iterator<Map.Entry<String, JsonNode>> nodes = binding.getFields();
                while (nodes.hasNext()) {
                    Map.Entry<String, JsonNode> entry = nodes.next();
                    JsonNode value = entry.getValue();
                    data.add(new Data(value.get("type").getTextValue(), value.get("value").getTextValue()));
                }
            }
            if(data.size() > MAX_DATA_SIZE) {
                data = data.subList(0, MAX_DATA_SIZE);
            }
            return data;
        }
        return null;
    }

    class ProcessedResponse {
        private static final String RESPONSE_CAROUSEL = "carousel";
        private static final String RESPONSE_TEXT = "text";

        private List<ResponseData> responseData = new ArrayList<>();
        private String responseType;

        public List<ResponseData> getResponseData() {
            return responseData;
        }

        public ProcessedResponse setResponseData(List<ResponseData> responseData) {
            this.responseData = responseData;
            return this;
        }

        public String getResponseType() {
            return responseType;
        }

        public ProcessedResponse setResponseType(String responseType) {
            this.responseType = responseType;
            return this;
        }

        public ProcessedResponse addResponseData(ResponseData responseData) {
            this.responseData.add(responseData);
            return this;
        }
    }

    class Data {
        private static final String URI = "uri";
        private static final String TYPED_LITERAL = "typed-literal";

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
