package chatbot.lib.handlers;

import chatbot.lib.Utility;
import chatbot.lib.request.ParameterType;
import chatbot.lib.request.Request;
import chatbot.lib.response.Response;
import chatbot.lib.response.ResponseData;
import chatbot.lib.response.ResponseGenerator;
import chatbot.lib.response.ResponseTemplates;
import chatbot.rivescript.RiveScriptBot;
import chatbot.rivescript.RiveScriptReplyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by ramgathreya on 5/23/17.
 */
public class ParameterHandler {
    private static final Logger logger = LoggerFactory.getLogger(ParameterHandler.class);
//    private String userId;
    private Request request;
    private String[] payload;
    private RiveScriptBot riveScriptBot;

    public ParameterHandler(Request request, String payload, RiveScriptBot riveScriptBot) {
        this.request = request;
        this.payload = Utility.split(payload);
        this.riveScriptBot = riveScriptBot;
    }

    public List<Response> handleParameterMessage() {
        ResponseGenerator responseGenerator = new ResponseGenerator();
        switch(this.payload[0]) {
            case ParameterType.START:
                responseGenerator.addTextResponse(new ResponseData(riveScriptBot.answer(this.request.getUserId(), RiveScriptReplyType.START_TEXT)[0]));
            case ParameterType.HELP:
                responseGenerator.addTextResponse(new ResponseData(riveScriptBot.answer(this.request.getUserId(), RiveScriptReplyType.HELP_TEXT)[0]));
                responseGenerator.addCarouselResponse(ResponseTemplates.getHelperTemplate());
                break;
            case ParameterType.CHECK_SERVICE:
                break;
            case ParameterType.DBPEDIA_ABOUT:
                responseGenerator.addTextResponse(new ResponseData("DBpedia is a crowd-sourced community effort to extract structured information from Wikipedia and make this information available on the Web."));
                responseGenerator.addButtonTextResponse(ResponseTemplates.getAboutDBpediaTemplate());
                break;
            case ParameterType.DBPEDIA_CONTRIBUTE:
                responseGenerator.addTextResponse(new ResponseData(riveScriptBot.answer(this.request.getUserId(), RiveScriptReplyType.DBPEDIA_CONTRIBUTE_TEXT)[0]));
                responseGenerator.addButtonTextResponse(ResponseTemplates.getContributeTemplate());
                break;
        }
        return responseGenerator.getResponse();
    }
}
