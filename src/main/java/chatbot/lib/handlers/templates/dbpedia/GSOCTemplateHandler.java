package chatbot.lib.handlers.templates.dbpedia;

import chatbot.Application;
import chatbot.lib.handlers.TemplateHandler;
import chatbot.lib.request.Request;
import chatbot.lib.request.TemplateType;
import chatbot.lib.response.ResponseData;
import chatbot.lib.response.ResponseGenerator;
import chatbot.lib.response.ResponseType;

import java.util.ArrayList;

/**
 * Created by ramgathreya on 7/12/17.
 */
public class GSOCTemplateHandler extends TemplateHandler{
    public GSOCTemplateHandler(Request request, String[] payload, Application.Helper helper) {
        super(request, payload, helper);
    }

    public ResponseGenerator handleTemplateMessage() {
        ResponseGenerator responseGenerator = new ResponseGenerator();
        switch (payload[0]) {
            // Links are specific to 2017 so needs to be changed next year
            case TemplateType.DBPEDIA_GSOC:
                responseGenerator.addTextResponse(new ResponseData("Google funds open source projects by paying students to work for three months on a specific task. The gain here is twofold:\n1 - Students gain experience and OS projects get some work done\n2 - (Possibly) new community members"));
                responseGenerator.addButtonTextResponse(new ResponseData("Here are some links you would find useful:", new ArrayList<ResponseData.Button>(){{
                    add(new ResponseData.Button("About GSoC", ResponseType.BUTTON_LINK, "http://wiki.dbpedia.org/gsoc"));
                    add(new ResponseData.Button("Ideas List", ResponseType.BUTTON_LINK, "http://wiki.dbpedia.org/ideas/ideas/scope:all/sort:activity-desc/tags:gsoc17/"));
                    add(new ResponseData.Button("How to Participate", ResponseType.BUTTON_LINK, "http://wiki.dbpedia.org/gsoc2017"));
                }}));
                break;
        }
        return responseGenerator;
    }
}
