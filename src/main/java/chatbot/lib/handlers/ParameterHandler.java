package chatbot.lib.handlers;

import chatbot.lib.Utility;
import chatbot.lib.request.ParameterType;
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
    private String userId;
    private String[] payload;
    private RiveScriptBot riveScriptBot;

    public ParameterHandler(String userId, String payload, RiveScriptBot riveScriptBot) {
        this.userId = userId;
        this.payload = Utility.split(payload);
        this.riveScriptBot = riveScriptBot;
    }

    public List<Response> handleParameterMessage() {
        ResponseGenerator response = new ResponseGenerator();
        switch(this.payload[0]) {
            case ParameterType.START:
                response.addTextResponse(new ResponseData(riveScriptBot.answer(this.userId, RiveScriptReplyType.START_TEXT)[0]));
            case ParameterType.HELP:
                response.addTextResponse(new ResponseData(riveScriptBot.answer(this.userId, RiveScriptReplyType.HELP_TEXT)[0]));
                response.addCarouselResponse(ResponseTemplates.getStarterTemplate());
                break;
            case ParameterType.DBPEDIA_ABOUT:
                response.addTextResponse(new ResponseData("DBpedia is a crowd-sourced community effort to extract structured information from Wikipedia and make this information available on the Web."));
                response.addButtonTextResponse(ResponseTemplates.getAboutDBpediaTemplate());
                break;
        }
        return response.getResponse();
    }
}
