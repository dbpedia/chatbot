package chatbot.lib.handlers;

import chatbot.Application;
import chatbot.lib.Constants;
import chatbot.lib.Utility;
import chatbot.lib.handlers.dbpedia.StatusCheckHandler;
import chatbot.lib.handlers.templates.dbpedia.*;
import chatbot.lib.handlers.templates.OptionsTemplateHandler;
import chatbot.lib.handlers.templates.entity.MovieTemplateHandler;
import chatbot.lib.handlers.templates.entity.TVTemplateHandler;
import chatbot.lib.request.TemplateType;
import chatbot.lib.request.Request;
import chatbot.lib.response.*;
import chatbot.rivescript.RiveScriptReplyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * Created by ramgathreya on 5/23/17.
 */
public class TemplateHandler {
    private static final Logger logger = LoggerFactory.getLogger(TemplateHandler.class);

    protected Request request;
    protected String[] payload;
    protected Application.Helper helper;

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

    public ResponseGenerator handleTemplateMessage() {
        ResponseGenerator responseGenerator = new ResponseGenerator();
        switch (payload[0]) {
            case TemplateType.START:
                responseGenerator.addTextResponse(new ResponseData(
                        helper.getRiveScriptBot().answer(this.request.getUserId(), RiveScriptReplyType.START_TEXT)[0]));
                responseGenerator.setShowFeedback(false);
            case TemplateType.HELP:
                responseGenerator.addTextResponse(new ResponseData(
                        helper.getRiveScriptBot().answer(this.request.getUserId(), RiveScriptReplyType.HELP_TEXT)[0]));
                responseGenerator.addCarouselResponse(getHelperTemplate());
                break;

            // Checking if a service is available
            case TemplateType.CHECK_SERVICE:
                responseGenerator = new StatusCheckHandler(request, payload[1], helper).handleStatusCheck();
                break;

            // DBpedia Template Scenarios
            case TemplateType.DBPEDIA_ABOUT:
            case TemplateType.DBPEDIA_CONTRIBUTE:
            case TemplateType.DBPEDIA_ASSOCIATION:
            case TemplateType.DBPEDIA_FALLBACK:
                responseGenerator = new DBpediaTemplateHandler(request, payload, helper).handleTemplateMessage();
                break;

            // DBpedia Datset Scenarios
            case TemplateType.DBPEDIA_DATASET:
            case TemplateType.DBPEDIA_DATASET_NLP:
            case TemplateType.DBPEDIA_DATABUS_RECOMMENDATION:
                responseGenerator = new DatasetTemplateHandler(request, payload, helper).handleTemplateMessage();
                break;

            // DBpedia Lookup Scenarios
            case TemplateType.DBPEDIA_LOOKUP:
            case TemplateType.DBPEDIA_LOOKUP_KEYWORD_SEARCH:
            case TemplateType.DBPEDIA_LOOKUP_PREFIX_SEARCH:
            case TemplateType.DBPEDIA_LOOKUP_PARAMETERS:
                responseGenerator = new LookupTemplateHandler(request, payload, helper).handleTemplateMessage();
                break;

            case TemplateType.DBPEDIA_MAPPINGS:
            case TemplateType.DBPEDIA_MAPPINGS_TOOL:
            case TemplateType.DBPEDIA_MAPPINGS_LOGIN:
                responseGenerator = new MappingsTemplateHandler(request, payload, helper).handleTemplateMessage();
                break;

            case TemplateType.DBPEDIA_EXTRACTION_FRAMEWORK:
                responseGenerator = new ExtractionTemplateHandler(request, payload, helper).handleTemplateMessage();
                break;

            case TemplateType.DBPEDIA_GSOC:
                responseGenerator = new GSOCTemplateHandler(request, payload, helper).handleTemplateMessage();
                break;

            // Further Options Scenario
            case TemplateType.LOAD_MORE:
            case TemplateType.LOAD_SIMILAR:
            case TemplateType.LOAD_RELATED:
            case TemplateType.LEARN_MORE:
                responseGenerator = new OptionsTemplateHandler(request, payload, helper).handleTemplateMessage();
                break;

            // TV Related Scenario
            case TemplateType.TV_CAST:
            case TemplateType.TV_CREW:
            case TemplateType.TV_SIMILAR:
            case TemplateType.TV_RELATED:
                responseGenerator = new TVTemplateHandler(request, payload, helper).handleTemplateMessage();
                break;

            // Movie Related Scenario
            case TemplateType.MOVIE_CAST:
            case TemplateType.MOVIE_CREW:
            case TemplateType.MOVIE_SIMILAR:
            case TemplateType.MOVIE_RELATED:
                responseGenerator = new MovieTemplateHandler(request, payload, helper).handleTemplateMessage();
                break;

            // Get Information for specific Entity
            case TemplateType.ENTITY_INFORMATION:
                responseGenerator.addCarouselResponse(new ArrayList<ResponseData>() {
                    {
                        add(helper.getSparql().getEntityInformation(payload[1]));
                    }
                });
                break;

            case TemplateType.GET_LOCATION:
                responseGenerator = new LocationHandler(request, payload[1], helper).getLocation();
                break;

            // When User Clicks Yes or No for Feedback Smart Reply
            case TemplateType.FEEDBACK:
                // Responses can be converted to rivescript
                switch (payload[1]) {
                    case TemplateType.YES:
                        responseGenerator.addTextResponse(new ResponseData("Glad that you like it."));
                        break;
                    case TemplateType.NO:
                        responseGenerator.addTextResponse(new ResponseData("Sorry, I will try to become better."));
                        break;
                }
                responseGenerator.setShowFeedback(false);
                break;

            // FAQs across services
            case TemplateType.FAQ:
                responseGenerator.addSmartReplyResponse(getFAQTemplate(payload[1]));
                break;
            default:
                responseGenerator.setFallbackResponse(request, helper.getRiveScriptBot());
        }
        return responseGenerator;
    }

    public ResponseData[] getHelperTemplate() {
        return new ResponseData[] {
                new ResponseData("/images/icon-dbpedia-92.png", "About DBpedia",
                        "You can ask me questions related to DBpedia such as:")
                        .addButton(new ResponseData.Button("What is DBpedia?", ResponseType.BUTTON_PARAM,
                                TemplateType.DBPEDIA_ABOUT))
                        .addButton(new ResponseData.Button("How do I contribute?", ResponseType.BUTTON_PARAM,
                                TemplateType.DBPEDIA_CONTRIBUTE))
                        .addButton(new ResponseData.Button("Is DBpedia down?", ResponseType.BUTTON_PARAM,
                                TemplateType.CHECK_SERVICE + Utility.STRING_SEPARATOR + Constants.DBPEDIA_SERVICE)),
                new ResponseData("/images/icon-help-92.png", "Factoid Questions",
                        "You can also ask me simple factual questions/trivia:")
                        .addButton(new ResponseData.Button("Who is Einstein?", ResponseType.BUTTON_PARAM,
                                TemplateType.ENTITY_INFORMATION + Utility.STRING_SEPARATOR
                                        + "http://dbpedia.org/resource/Albert_Einstein"))
                        .addButton(new ResponseData.Button("What is a planet?", ResponseType.BUTTON_PARAM,
                                TemplateType.ENTITY_INFORMATION + Utility.STRING_SEPARATOR
                                        + "http://dbpedia.org/resource/Planet"))
                        .addButton(new ResponseData.Button("Where is Berlin?", ResponseType.BUTTON_PARAM,
                                TemplateType.GET_LOCATION + Utility.STRING_SEPARATOR + "Berlin"))
        };
    }

    public ResponseData getFAQTemplate(String serviceName) {
        String[] service = Constants.SERVICES.get(serviceName);
        ResponseData responseData = new ResponseData(
                "Here are some frequently asked questions about " + service[0] + ":");
        switch (serviceName) {
            case Constants.DBPEDIA_SERVICE:
                responseData.addSmartReply(new ResponseData.SmartReply("What is DBpedia?", TemplateType.DBPEDIA_ABOUT));
                responseData.addSmartReply(
                        new ResponseData.SmartReply("How can I Contribute?", TemplateType.DBPEDIA_CONTRIBUTE));
                responseData.addSmartReply(new ResponseData.SmartReply("Is DBpedia Working?",
                        TemplateType.CHECK_SERVICE + Utility.STRING_SEPARATOR + serviceName));
                break;
            case Constants.DBPEDIA_LOOKUP_SERVICE:
                responseData.addSmartReply(
                        new ResponseData.SmartReply("What is DBpedia Lookup?", TemplateType.DBPEDIA_LOOKUP));
                responseData.addSmartReply(new ResponseData.SmartReply("Tell me about Lookup Parameters.",
                        TemplateType.DBPEDIA_LOOKUP_PARAMETERS));
                responseData.addSmartReply(new ResponseData.SmartReply("What is Lookup PrefixSearch?",
                        TemplateType.DBPEDIA_LOOKUP_PREFIX_SEARCH));
                break;
            case Constants.DBPEDIA_MAPPINGS_SERVICE:
                responseData.addSmartReply(
                        new ResponseData.SmartReply("What is Mappings Wiki?", TemplateType.DBPEDIA_MAPPINGS));
                responseData.addSmartReply(new ResponseData.SmartReply("Where can I find the Mapping Tool?",
                        TemplateType.DBPEDIA_MAPPINGS_TOOL));
                responseData.addSmartReply(new ResponseData.SmartReply("Is Mappings Wiki Down?",
                        TemplateType.CHECK_SERVICE + Utility.STRING_SEPARATOR + serviceName));
                break;
        }
        return responseData;
    }
}
