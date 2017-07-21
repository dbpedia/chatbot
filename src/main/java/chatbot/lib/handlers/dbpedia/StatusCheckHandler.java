package chatbot.lib.handlers.dbpedia;

import chatbot.Application;
import chatbot.lib.Constants;
import chatbot.platforms.Platform;
import chatbot.lib.Utility;
import chatbot.lib.api.StatusCheckService;
import chatbot.lib.request.Request;
import chatbot.lib.response.ResponseData;
import chatbot.lib.response.ResponseGenerator;
import chatbot.lib.response.ResponseType;
import chatbot.rivescript.RiveScriptReplyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * Created by ramgathreya on 6/12/17.
 */
public class StatusCheckHandler {
    private static final Logger logger = LoggerFactory.getLogger(StatusCheckHandler.class);

    private StatusCheckService statusCheckService = new StatusCheckService();
    private Request request;
    private ArrayList<String[]> service = new ArrayList<>();
    private Application.Helper helper;

    public StatusCheckHandler(Request request, String service, Application.Helper helper) {
        this.request = request;
        this.helper = helper;

        switch(service) {
            case Constants.DBPEDIA_SERVICE:
                this.service.add(Constants.SERVICES.get(Constants.DBPEDIA_SERVICE));
                this.service.add(Constants.SERVICES.get(Constants.DBPEDIA_RESOURCE_SERVICE));
                this.service.add(Constants.SERVICES.get(Constants.DBPEDIA_SPARQL_SERVICE));
                break;
            case Constants.DBPEDIA_LOOKUP_SERVICE:
                this.service.add(Constants.SERVICES.get(Constants.DBPEDIA_LOOKUP_KEYWORD_SEARCH_SERVICE));
                this.service.add(Constants.SERVICES.get(Constants.DBPEDIA_LOOKUP_PREFIX_SEARCH_SERVICE));
                break;
            case Constants.DBPEDIA_MAPPINGS_SERVICE:
                this.service.add(Constants.SERVICES.get(Constants.DBPEDIA_MAPPINGS_SERVICE));
                this.service.add(Constants.SERVICES.get(Constants.DBPEDIA_MAPPINGS_SERVER_SERVICE));
                break;
            default:
                this.service.add(Constants.SERVICES.get(service));
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

    public ResponseGenerator handleStatusCheck() {
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
            replies = helper.getRiveScriptBot().answer(request.getUserId(), RiveScriptReplyType.STATUS_CHECK_TEXT + " " + serviceName + " " + statusCheck[0]);
        }

        // Facebook does not support mailto: links so for that we are simply redirecting to the mailing list website
        if (request.getPlatform().equals(Platform.FB)) {
            contactUri = service.get(0)[3];
        }
        else {
            contactUri = "mailto:" + mailingList + "?subject=" + statusCheck[1];
        }

        responseGenerator.addTextResponse(new ResponseData(replies[0]));
        responseGenerator.addButtonTextResponse(new ResponseData(replies[1], new ArrayList<ResponseData.Button>(){{
            add(new ResponseData.Button("Contact Us", ResponseType.BUTTON_LINK, contactUri));
        }}));
        return responseGenerator;
    }
}
