package chatbot.lib.handlers.templates.dbpedia;

import chatbot.Application;
import chatbot.lib.Constants;
import chatbot.lib.Utility;
import chatbot.lib.handlers.TemplateHandler;
import chatbot.lib.request.Request;
import chatbot.lib.request.TemplateType;
import chatbot.lib.response.ResponseData;
import chatbot.lib.response.ResponseGenerator;
import chatbot.lib.response.ResponseType;
import chatbot.rivescript.RiveScriptReplyType;

import java.util.ArrayList;

/**
 * Created by ramgathreya on 7/3/17.
 */
public class DBpediaTemplateHandler extends TemplateHandler {
    public DBpediaTemplateHandler(Request request, String[] payload, Application.Helper helper) {
        super(request, payload, helper);
    }

    public ResponseGenerator handleTemplateMessage() {
        ResponseGenerator responseGenerator = new ResponseGenerator();
        switch(payload[0]) {
            case TemplateType.DBPEDIA_ABOUT:
                responseGenerator.addTextResponse(new ResponseData("DBpedia is a crowd-sourced community effort to extract structured information from Wikipedia and make this information available on the Web."));
                responseGenerator.addButtonTextResponse(new ResponseData("You can find more information here:", new ArrayList<ResponseData.Button>(){{
                    add(new ResponseData.Button("About DBpedia", ResponseType.BUTTON_LINK, "http://wiki.dbpedia.org/about"));
                    add(new ResponseData.Button("Getting Started", ResponseType.BUTTON_LINK, "http://wiki.dbpedia.org/get-involved"));
                    add(new ResponseData.Button("Tutorial", ResponseType.BUTTON_LINK, "http://semanticweb.org/wiki/Getting_data_from_the_Semantic_Web.html"));
                }}));
                break;
            case TemplateType.DBPEDIA_CONTRIBUTE:
                responseGenerator.addTextResponse(new ResponseData(helper.getRiveScriptBot().answer(request.getUserId(), RiveScriptReplyType.DBPEDIA_CONTRIBUTE_TEXT)[0]));
                responseGenerator.addButtonTextResponse(new ResponseData("You can:\n1 - Look at open issues if you want to contribute to the codebase\n2 - Improve Documentation\n3 - Join the discussion on upcoming features, releases and issues", new ArrayList<ResponseData.Button>(){{
                    add(new ResponseData.Button("Get Involved", ResponseType.BUTTON_LINK, "http://wiki.dbpedia.org/get-involved"));
                    add(new ResponseData.Button("Mailing List", ResponseType.BUTTON_LINK, "https://lists.sourceforge.net/lists/listinfo/dbpedia-discussion"));
                    add(new ResponseData.Button("Slack", ResponseType.BUTTON_LINK, "https://dbpedia.slack.com/"));
                }}));
                break;
            case TemplateType.DBPEDIA_FALLBACK:
                String[] service = Constants.SERVICES.get(payload[1]);
                responseGenerator.addButtonTextResponse(new ResponseData("Sorry, I didn't completely understand your question about " + service[0] + ". Maybe someone in the Mailing List can help you better.", new ArrayList<ResponseData.Button>(){{
                    add(new ResponseData.Button("Mailing List", ResponseType.BUTTON_LINK, service[3]));
                    add(new ResponseData.Button("FAQ", ResponseType.BUTTON_PARAM, TemplateType.FAQ + Utility.STRING_SEPARATOR + payload[1]));
                }}));
                responseGenerator.setShowFeedback(false);
                break;
        }
        return responseGenerator;
    }
}
