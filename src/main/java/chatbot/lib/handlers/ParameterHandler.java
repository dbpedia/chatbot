package chatbot.lib.handlers;

import chatbot.lib.Utility;
import chatbot.lib.api.GenesisService;
import chatbot.lib.api.SPARQL;
import chatbot.lib.request.ParameterType;
import chatbot.lib.request.Request;
import chatbot.lib.response.*;
import chatbot.rivescript.RiveScriptBot;
import chatbot.rivescript.RiveScriptReplyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ramgathreya on 5/23/17.
 */
public class ParameterHandler {
    private static final Logger logger = LoggerFactory.getLogger(ParameterHandler.class);

    private Request request;
    private String[] payload;
    private RiveScriptBot riveScriptBot;

    public ParameterHandler(Request request, String payload, RiveScriptBot riveScriptBot) {
        this.request = request;
        this.payload = Utility.split(payload);
        this.riveScriptBot = riveScriptBot;
    }

    private ResponseGenerator getSimilarOrRelatedEntities(ResponseGenerator responseGenerator, String scenario, String uri) {
        SPARQL sparql = new SPARQL();
        String uris = null;

        switch (scenario) {
            case ParameterType.LOAD_SIMILAR:
                uris = new GenesisService().getSimilarEntities(uri);
                break;
            case ParameterType.LOAD_RELATED:
                uris = new GenesisService().getRelatedEntities(uri);
                break;
        }
        // Set Fallback when GENESIS returns null or invalid response
        if(uris.isEmpty() || uris == " " || uris == null) {
            return responseGenerator.setFallbackResponse(request, riveScriptBot);
        }
        return responseGenerator.addCarouselResponse(sparql.getEntitiesByURIs(uris));
    }

    public ResponseGenerator handleParameterMessage() throws IOException {
        ResponseGenerator responseGenerator = new ResponseGenerator();
        switch(payload[0]) {
            case ParameterType.START:
                responseGenerator.addTextResponse(new ResponseData(riveScriptBot.answer(this.request.getUserId(), RiveScriptReplyType.START_TEXT)[0]));
                responseGenerator.setShowFeedback(false);
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
            case ParameterType.LOAD_MORE:
                SPARQL.ResponseInfo responseInfo = Utility.toObject(payload[1], SPARQL.ResponseInfo.class);
                responseGenerator.addCarouselResponse(responseInfo.nextPage());
                // Pagination
                if (responseInfo.hasMorePages()) {
                    responseInfo.next();
                    responseGenerator.addButtonTextResponse(new ResponseData(riveScriptBot.answer(request.getUserId(), RiveScriptReplyType.LOAD_MORE_TEXT)[0], new ArrayList<ResponseData.Button>(){{
                        add(new ResponseData.Button("Load More", ResponseType.BUTTON_PARAM, ParameterType.LOAD_MORE + Utility.STRING_SEPARATOR + Utility.toJson(responseInfo)));
                    }}));
                }
                break;
            case ParameterType.LOAD_SIMILAR:
            case ParameterType.LOAD_RELATED:
                responseGenerator = getSimilarOrRelatedEntities(responseGenerator, payload[0], payload[1]);
                break;
            case ParameterType.LEARN_MORE:
                responseGenerator.addSmartReplyResponse(new ResponseData()
                        .setText(riveScriptBot.answer(request.getUserId(), RiveScriptReplyType.LEARN_MORE_TEXT + " " + payload[2])[0])
                        .addSmartReply(new ResponseData.SmartReply("Similar", ParameterType.LOAD_SIMILAR + Utility.STRING_SEPARATOR + payload[1]))
                        .addSmartReply(new ResponseData.SmartReply("Related", ParameterType.LOAD_RELATED + Utility.STRING_SEPARATOR + payload[1]))
                );
                break;
            case ParameterType.FEEDBACK:
                // Responses can be converted to rivescript
                switch (payload[1]) {
                    case ParameterType.YES:
                        responseGenerator.addTextResponse(new ResponseData("Glad that you like it."));
                        break;
                    case ParameterType.NO:
                        responseGenerator.addTextResponse(new ResponseData("Sorry, I will try better."));
                        break;
                }
                responseGenerator.setShowFeedback(false);
                break;
        }
        return responseGenerator;
    }
}
