package chatbot.lib.handlers;

import chatbot.Application;
import chatbot.lib.Utility;
import chatbot.lib.api.qa.QAService;
import chatbot.lib.api.SPARQL;
import chatbot.lib.request.TemplateType;
import chatbot.lib.request.Request;
import chatbot.lib.response.ResponseData;
import chatbot.lib.response.ResponseGenerator;
import chatbot.lib.response.ResponseType;
import chatbot.rivescript.RiveScriptReplyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by ramgathreya on 6/2/17.
 */
public class NLHandler {
    private static final Logger logger = LoggerFactory.getLogger(NLHandler.class);

    private String question;
    private QAService qaService;

    private Request request;
    private Application.Helper helper;

    public NLHandler(Request request, String question, Application.Helper helper) {
        this.question = question;
        this.qaService = new QAService(helper.getWolframRepository());

        this.request = request;
        this.helper = helper;
    }

    public ResponseGenerator answer() throws Exception {
        ResponseGenerator responseGenerator = new ResponseGenerator();
        QAService.Data data = qaService.search(question);
        SPARQL.ProcessedResponse processedResponse = processResponseData(data);
        List<ResponseData> responseDatas = processedResponse.getResponseData();

        // When there is a valid response
        if (responseDatas.size() > 0) {
            switch (processedResponse.getResponseType()) {
                case SPARQL.ProcessedResponse.RESPONSE_TEXT:
                    responseGenerator.addTextResponse(responseDatas.get(0));
                    break;
                case SPARQL.ProcessedResponse.RESPONSE_CAROUSEL:
                    SPARQL.ResponseInfo responseInfo = processedResponse.getResponseInfo();
                    switch (responseInfo.getQueryResultType()) {
                        case SPARQL.ResponseInfo.DISAMBIGUATION_PAGE:
                            responseGenerator.addTextResponse(new ResponseData(helper.getRiveScriptBot().answer(request.getUserId(), RiveScriptReplyType.DISAMBIGUATION_TEXT)[0]));
                            break;
                        default:
                            responseGenerator.addTextResponse(new ResponseData(helper.getRiveScriptBot().answer(request.getUserId(), RiveScriptReplyType.NL_ANSWER_TEXT)[0]));
                    }
                    responseGenerator.addCarouselResponse(responseDatas.toArray(new ResponseData[responseDatas.size()]));

                    // Pagination
                    if (responseInfo.hasMorePages()) {
                        responseInfo.next();
                        responseGenerator.addButtonTextResponse(new ResponseData(helper.getRiveScriptBot().answer(request.getUserId(), RiveScriptReplyType.LOAD_MORE_TEXT)[0], new ArrayList<ResponseData.Button>() {{
                            add(new ResponseData.Button("Load More", ResponseType.BUTTON_PARAM, TemplateType.LOAD_MORE + Utility.STRING_SEPARATOR + Utility.toJson(responseInfo)));
                        }}));
                    }
                    break;
                default:
//                    responseGenerator.setFallbackResponse(request, helper.getRiveScriptBot());
            }
        }
        else {
//            responseGenerator.setFallbackResponse(request, helper.getRiveScriptBot());
        }
        return responseGenerator;
    }

    private SPARQL.ProcessedResponse processResponseData(QAService.Data data) {
        SPARQL.ProcessedResponse processedResponse = new SPARQL.ProcessedResponse();
        if(data != null) {
            int count;

            HashSet<String> literals = data.getLiterals();
            HashSet<String> uris = data.getUris();

            if(literals.size() > 0) {
                String result = "";
                for(String literal : literals) {
                    result += literal + "\n";
                }
                final String finalResult = result.substring(0, result.length() - 1);
                processedResponse.setResponseType(SPARQL.ProcessedResponse.RESPONSE_TEXT);
                processedResponse.setResponseData(new ArrayList<ResponseData>() {{
                    add(new ResponseData(finalResult));
                }});
            }
            else if(uris.size() > 0) {
                for(String uri : uris) {
                    // Restrict to MAX_DATA_SIZE Elements
                    if(processedResponse.getResponseData().size() == ResponseData.MAX_DATA_SIZE) {
                        break;
                    }

                    count = helper.getSparql().isDisambiguationPage(uri);
                    processedResponse.setResponseType(SPARQL.ProcessedResponse.RESPONSE_CAROUSEL);

                    // Not a disambiguation page
                    if(count == 0) {
                        ResponseData _data = helper.getSparql().getEntityInformation(uri);
                        if (_data != null) {
                            processedResponse.addResponseData(_data);
                        }
                    }
                    // Disambiguation page
                    else {
                        processedResponse.getResponseInfo().setUri(uri).setCount(count).setQueryResultType(SPARQL.ResponseInfo.DISAMBIGUATION_PAGE).setOffset(0).setLimit(ResponseData.MAX_DATA_SIZE);
                        processedResponse.setResponseData(helper.getSparql().getDisambiguatedEntities(uri, 0, ResponseData.MAX_DATA_SIZE));
                        return processedResponse;
                    }
                }
            }
        }
        return processedResponse;
    }
}
