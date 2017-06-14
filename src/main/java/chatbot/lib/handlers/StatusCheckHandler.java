package chatbot.lib.handlers;

import chatbot.lib.Platform;
import chatbot.lib.Utility;
import chatbot.lib.api.StatusCheckService;
import chatbot.lib.request.Request;
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

    // Services that can be tested
    private static final String DBPEDIA_SERVICE = "dbpedia";
    private static final String DBPEDIA_RESOURCE_SERVICE = "dbpedia-resource";
    private static final String DBPEDIA_SPARQL_SERVICE = "dbpedia-sparql";

    // Corresponding Service Names
    private static final String DBPEDIA = "DBpedia";
    private static final String DBPEDIA_RESOURCE = "DBpedia Resource";
    private static final String DBPEDIA_SPARQL = "DBpedia SPARQL";

    private StatusCheckService statusCheckService = new StatusCheckService();
    private Request request;
    private ArrayList<String[]> service = new ArrayList<>();
    private RiveScriptBot riveScriptBot;

    private static final HashMap<String, String[]> ENDPOINTS = new HashMap<String, String[]>(){{
        // Service Name, Endpoint URL, Mailing List
        put(DBPEDIA_SERVICE, new String[]{DBPEDIA, "http://wiki.dbpedia.org", "dbpedia-discussion@lists.sourceforge.net", "https://lists.sourceforge.net/lists/listinfo/dbpedia-discussion"});
        put(DBPEDIA_RESOURCE_SERVICE, new String[]{DBPEDIA_RESOURCE, "http://dbpedia.org/page/DBpedia", "dbpedia-discussion@lists.sourceforge.net", "https://lists.sourceforge.net/lists/listinfo/dbpedia-discussion"});
        put(DBPEDIA_SPARQL_SERVICE, new String[]{DBPEDIA_SPARQL, "http://dbpedia.org/sparql?default-graph-uri=http%3A%2F%2Fdbpedia.org&query=select+distinct+%3FConcept+where+%7B%5B%5D+a+%3FConcept%7D+LIMIT+100&format=text%2Fhtml&CXML_redir_for_subjs=121&CXML_redir_for_hrefs=&timeout=30000&debug=on", "dbpedia-discussion@lists.sourceforge.net", "https://lists.sourceforge.net/lists/listinfo/dbpedia-discussion"});
    }};

    public StatusCheckHandler(Request request, String service, RiveScriptBot riveScriptBot) {
        this.request = request;
        this.riveScriptBot = riveScriptBot;

        switch(service) {
            case DBPEDIA_SERVICE:
                this.service.add(ENDPOINTS.get(DBPEDIA_SERVICE));
                this.service.add(ENDPOINTS.get(DBPEDIA_RESOURCE_SERVICE));
                this.service.add(ENDPOINTS.get(DBPEDIA_SPARQL_SERVICE));
                break;
            default:
                this.service.add(ENDPOINTS.get(service));
        }
    }

    private String[] makeStatusCheck(String serviceName) {
        String status, subject, message = "";
        if(this.service.size() > 1) {
            boolean allWorking = true;
            for (String[] service : this.service) {
                if(statusCheckService.setUrl(service[1]).isOnline()) {
                    message += service[0] + " is UP\n";
                }
                else {
                    message += service[0] + " is DOWN\n";
                    allWorking = false;
                }
            }

            if(allWorking) {
                status = message + Utility.STRING_SEPARATOR + "Please try again. If you are still facing issues reach out to us.";
                subject = "Please Help: I am facing issues connecting to " + serviceName;
            }
            else {
                status = message + Utility.STRING_SEPARATOR + "We are looking into it. You can help us by sending a message.";
                subject = "IMPORTANT: " + serviceName + " seems to be down!!!";
            }
        }
        else {
            if(statusCheckService.setUrl(this.service.get(0)[1]).isOnline()) {
                status = RiveScriptReplyType.YES_TEXT;
                subject = "Please Help: I am facing issues connecting to " + serviceName;
            }
            else {
                status = RiveScriptReplyType.NO_TEXT;
                subject = "IMPORTANT: " + serviceName + " seems to be down!!!";
            }
        }
        return new String[]{status, subject};
    }

    public List<Response> handleStatusCheck() {
        ResponseGenerator responseGenerator = new ResponseGenerator();
        String serviceName = service.get(0)[0];
        String mailingList = service.get(0)[2];
        String[] replies = null;
        String contactUri;

        String[] statusCheck = makeStatusCheck(serviceName); // 1st element is status and 2nd element is subject for the mailing list and 3rd element is message which needs to be used only in the case where multiple services are called
        if (service.size() > 1) {
            replies = Utility.split(statusCheck[0]);
        }
        else {
            replies = riveScriptBot.answer(request.getUserId(), RiveScriptReplyType.STATUS_CHECK_TEXT + " " + serviceName + " " + statusCheck[0]);
        }

        // Facebook does not support mailto: links so for that we are simply redirecting to the mailing list website
        if (request.getPlatform().equals(Platform.FB)) {
            contactUri = service.get(0)[3];
        }
        else {
            contactUri = "mailto:" + mailingList + "?subject=" + statusCheck[1];
        }

        responseGenerator.addTextResponse(new ResponseData(replies[0]));
        responseGenerator.addButtonTextResponse(new ResponseData(replies[1], new ArrayList<ResponseData.ButtonData>(){{
            add(new ResponseData.ButtonData("Contact Us", ResponseType.BUTTON_LINK, contactUri));
        }}));
        return responseGenerator.getResponse();
    }
}
