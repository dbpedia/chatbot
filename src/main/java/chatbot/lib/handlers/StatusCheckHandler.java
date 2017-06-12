package chatbot.lib.handlers;

import chatbot.lib.api.StatusCheckService;
import chatbot.lib.response.Response;
import chatbot.lib.response.ResponseData;
import chatbot.lib.response.ResponseGenerator;
import chatbot.lib.response.ResponseType;
import chatbot.rivescript.RiveScriptBot;
import chatbot.rivescript.RiveScriptReplyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ramgathreya on 6/12/17.
 */
public class StatusCheckHandler {
    private static final Logger logger = LoggerFactory.getLogger(StatusCheckHandler.class);
    private static final String DBPEDIA_SERVICE = "dbpedia";

    private StatusCheckService statusCheckService;
    private String userId;
    private String[] service;
    private RiveScriptBot riveScriptBot;

    private static final HashMap<String, String[]> ENDPOINTS = new HashMap<String, String[]>(){{
        put(DBPEDIA_SERVICE, new String[]{"DBpedia", "http://dbpedia.org/page/DBpedia"});
    }};

    public StatusCheckHandler(String userId, String service, RiveScriptBot riveScriptBot) {
        this.userId = userId;
        this.service = ENDPOINTS.get(service);
        this.riveScriptBot = riveScriptBot;
        this.statusCheckService = new StatusCheckService(this.service[1]);
    }

    public List<Response> handleStatusCheck() {
        ResponseGenerator responseGenerator = new ResponseGenerator();
        String serviceName = service[0];
        String status, subject;

        if(statusCheckService.isOnline() == true) {
            status = RiveScriptReplyType.YES_TEXT;
            subject = "Please Help: I am facing issues connecting to " + serviceName;
        }
        else {
            status = RiveScriptReplyType.NO_TEXT;
            subject = "IMPORTANT: " + serviceName + " seems to be down!!!";
        }

        String [] replies = riveScriptBot.answer(userId, RiveScriptReplyType.STATUS_CHECK_TEXT + " " + status + " " + serviceName);
        responseGenerator.addTextResponse(new ResponseData(replies[0]));
        responseGenerator.addButtonTextResponse(new ResponseData(replies[1], new ArrayList<ResponseData.ButtonData>(){{
            add(new ResponseData.ButtonData("Contact Us", ResponseType.BUTTON_LINK, "mailto:dbpedia-discussion@lists.sourceforge.net?subject=" + subject));
        }}));
        return responseGenerator.getResponse();
    }
}
