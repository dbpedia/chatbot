package chatbot.lib.response;

import chatbot.Application;
import chatbot.lib.Constants;
import chatbot.lib.Utility;
import chatbot.lib.api.SPARQL;
import chatbot.lib.api.dbpedia.GenesisService;
import chatbot.lib.request.Request;
import chatbot.lib.request.TemplateType;

import java.util.ArrayList;

/**
 * Created by ramgathreya on 5/23/17.
 */
public class ResponseTemplates {
    public static ResponseData[] getHelperTemplate() {
        return new ResponseData[]{
                new ResponseData("/images/icon-dbpedia-92.png", "About DBpedia", "What is DBpedia?\nHow can I contribute to DBpedia?"),
                new ResponseData("/images/icon-user-92.png", "Who ?", "Albert Einstein\nTell me about Barack Obama"),
                new ResponseData("/images/icon-help-92.png", "What ?", "What is a planet?\nWhat is Mathematics?"),
                new ResponseData("/images/icon-compass-92.png", "Where ?", "Where is the Eiffel Tower?\nWhere is Germany's capital?")
        };
    }

    public static ResponseData getFAQTemplate(String serviceName) {
        String[] service = Constants.SERVICES.get(serviceName);
        ResponseData responseData = new ResponseData("Here are some frequently asked questions about " + service[0] + ":");
        switch(serviceName) {
            case Constants.DBPEDIA_SERVICE:
                responseData.addSmartReply(new ResponseData.SmartReply("What is DBpedia?", TemplateType.DBPEDIA_ABOUT));
                responseData.addSmartReply(new ResponseData.SmartReply("How can I Contribute?", TemplateType.DBPEDIA_CONTRIBUTE));
                responseData.addSmartReply(new ResponseData.SmartReply("Is DBpedia Working?", TemplateType.CHECK_SERVICE + Utility.STRING_SEPARATOR + serviceName));
                break;
        }
        return responseData;
    }
}
