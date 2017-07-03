package chatbot.lib.handlers;

import chatbot.Application;
import chatbot.lib.Ontology;
import chatbot.lib.Utility;
import chatbot.lib.api.TMDBService;
import chatbot.lib.api.dbpedia.GenesisService;
import chatbot.lib.api.SPARQL;
import chatbot.lib.handlers.dbpedia.StatusCheckHandler;
import chatbot.lib.handlers.templates.DBpediaTemplateHandler;
import chatbot.lib.handlers.templates.OptionsTemplateHandler;
import chatbot.lib.handlers.templates.entity.MovieTemplateHandler;
import chatbot.lib.handlers.templates.entity.TVTemplateHandler;
import chatbot.lib.request.TemplateType;
import chatbot.lib.request.Request;
import chatbot.lib.response.*;
import chatbot.rivescript.RiveScriptReplyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by ramgathreya on 5/23/17.
 */
public class TemplateHandler {
    private static final Logger logger = LoggerFactory.getLogger(TemplateHandler.class);

    private Request request;
    private String[] payload;
    private Application.Helper helper;
    
    public TemplateHandler(Request request, String payload, Application.Helper helper) {
        this.request = request;
        this.payload = Utility.split(payload);
        this.helper = helper;
    }

    public TemplateHandler(Request request, String[] payload, Application.Helper helper) {
        this.request = request;
        this.payload = payload;
        this.helper = helper;
    }

    public ResponseGenerator handleParameterMessage() {
        ResponseGenerator responseGenerator = new ResponseGenerator();
        switch(payload[0]) {
            case TemplateType.START:
                responseGenerator.addTextResponse(new ResponseData(helper.getRiveScriptBot().answer(this.request.getUserId(), RiveScriptReplyType.START_TEXT)[0]));
                responseGenerator.setShowFeedback(false);
            case TemplateType.HELP:
                responseGenerator.addTextResponse(new ResponseData(helper.getRiveScriptBot().answer(this.request.getUserId(), RiveScriptReplyType.HELP_TEXT)[0]));
                responseGenerator.addCarouselResponse(ResponseTemplates.getHelperTemplate());
                break;
            case TemplateType.CHECK_SERVICE:
                responseGenerator = new StatusCheckHandler(request, payload[1], helper).handleStatusCheck();
                break;

            // All DBpedia Template Scenarios
            case TemplateType.DBPEDIA_ABOUT:
            case TemplateType.DBPEDIA_CONTRIBUTE:
            case TemplateType.DBPEDIA_FALLBACK:
                responseGenerator = new DBpediaTemplateHandler(request, payload, helper).handleDBpediaTemplateMessage();
                break;

            // Further Options Scenario
            case TemplateType.LOAD_MORE:
            case TemplateType.LOAD_SIMILAR:
            case TemplateType.LOAD_RELATED:
            case TemplateType.LEARN_MORE:
                responseGenerator = new OptionsTemplateHandler(request, payload, helper).handleOptionsTemplateMessage();
                break;

            // TV Related Scenario
            case TemplateType.TV_CAST:
            case TemplateType.TV_CREW:
            case TemplateType.TV_SIMILAR:
            case TemplateType.TV_RELATED:
                responseGenerator = new TVTemplateHandler(request, payload, helper).handTVTemplateMessage();
                break;

            // Movie Related Scenario
            case TemplateType.MOVIE_CAST:
            case TemplateType.MOVIE_CREW:
            case TemplateType.MOVIE_SIMILAR:
            case TemplateType.MOVIE_RELATED:
                responseGenerator = new MovieTemplateHandler(request, payload, helper).handleMovieTemplateMessage();
                break;

            // When User Clicks Yes or No for Feedback Smart Reply
            case TemplateType.FEEDBACK:
                // Responses can be converted to rivescript
                switch (payload[1]) {
                    case TemplateType.YES:
                        responseGenerator.addTextResponse(new ResponseData("Glad that you like it."));
                        break;
                    case TemplateType.NO:
                        responseGenerator.addTextResponse(new ResponseData("Sorry, I will try better."));
                        break;
                }
                responseGenerator.setShowFeedback(false);
                break;

            // FAQs across services
            case TemplateType.FAQ:
                responseGenerator.addSmartReplyResponse(ResponseTemplates.getFAQTemplate(payload[1]));
                break;
            default:
                responseGenerator.setFallbackResponse(request, helper.getRiveScriptBot());
        }
        return responseGenerator;
    }
}
