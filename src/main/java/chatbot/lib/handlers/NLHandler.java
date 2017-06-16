package chatbot.lib.handlers;

import chatbot.lib.Utility;
import chatbot.lib.api.QAService;
import chatbot.lib.api.SPARQL;
import chatbot.lib.request.ParameterType;
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
        List<QAService.Data> data = qaService.search(question);
        SPARQL.ProcessedResponse processedResponse = processResponseData(data);
        List<ResponseData> responseDatas = processedResponse.getResponseData();

        switch(processedResponse.getResponseType()) {
            case SPARQL.ProcessedResponse.RESPONSE_TEXT:
                responseGenerator.addTextResponse(responseDatas.get(0));
                break;
            case SPARQL.ProcessedResponse.RESPONSE_CAROUSEL:
                SPARQL.ProcessedResponse.ResponseInfo responseInfo = processedResponse.getResponseInfo();
                switch(responseInfo.getQueryResultType()) {
                    case SPARQL.ProcessedResponse.ResponseInfo.DISAMBIGUATION_PAGE:
                        responseGenerator.addTextResponse(new ResponseData(riveScriptBot.answer(request.getUserId(), RiveScriptReplyType.NL_ANSWER_TEXT + " " + RiveScriptReplyType.DISAMBIGUATION_TEXT)[0]));
                        break;
                    default:
                        responseGenerator.addTextResponse(new ResponseData(riveScriptBot.answer(request.getUserId(), RiveScriptReplyType.NL_ANSWER_TEXT)[0]));
                }
                responseGenerator.addCarouselResponse(responseDatas.toArray(new ResponseData[responseDatas.size()]));

                // Pagination
                if (responseInfo.hasMorePages()) {
                    responseInfo.next();
                    responseGenerator.addButtonTextResponse(new ResponseData(riveScriptBot.answer(request.getUserId(), RiveScriptReplyType.NL_ANSWER_TEXT + " " + RiveScriptReplyType.LOAD_MORE_TEXT)[0], new ArrayList<ResponseData.ButtonData>(){{
                        add(new ResponseData.ButtonData("Load More", ResponseType.BUTTON_PARAM, ParameterType.LOAD_MORE + Utility.STRING_SEPARATOR + Utility.toJson(responseInfo)));
                    }}));
                }
                break;
            default:
                responseGenerator.addTextResponse(new ResponseData(riveScriptBot.answer(request.getUserId(), RiveScriptReplyType.FALLBACK_TEXT)[0]));
        }
        return responseGenerator.getResponse();
    }

    private SPARQL.ProcessedResponse processResponseData(List<QAService.Data> data) {
        SPARQL.ProcessedResponse processedResponse = new SPARQL.ProcessedResponse();
        if(data != null && data.size() > 0) {
            String uri;
            int count;
            for(QAService.Data item : data) {
                switch(item.getType()) {
                    case QAService.Data.TYPED_LITERAL:
                        processedResponse.setResponseType(SPARQL.ProcessedResponse.RESPONSE_TEXT);
                        processedResponse.setResponseData(new ArrayList<ResponseData>() {{
                            add(new ResponseData(item.getValue()));
                        }});
                        return processedResponse;
                    case QAService.Data.URI:
                        uri = item.getValue();
                        count = sparql.isDisambiguationPage(uri);
                        processedResponse.setResponseType(SPARQL.ProcessedResponse.RESPONSE_CAROUSEL);

                        // Not a disambiguation page
                        if(count == 0) {
                            ResponseData _data = sparql.getEntityInformation(item.getValue());
                            if (_data != null) {
                                processedResponse.addResponseData(_data);
                            }
                        }
                        // Disambiguation page
                        else {
                            processedResponse.getResponseInfo().setUri(uri).setCount(count).setQueryResultType(SPARQL.ProcessedResponse.ResponseInfo.DISAMBIGUATION_PAGE).setOffset(0).setLimit(ResponseData.MAX_DATA_SIZE);
                            processedResponse.setResponseData(sparql.getDisambiguatedEntities(uri, 0, ResponseData.MAX_DATA_SIZE));
                            return processedResponse;
                        }
                        break;
                }
            }
        }
        return processedResponse;
    }
}
