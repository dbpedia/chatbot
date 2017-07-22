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
public class ExtractionTemplateHandler extends TemplateHandler{
    public ExtractionTemplateHandler(Request request, String[] payload, Application.Helper helper) {
        super(request, payload, helper);
    }

    public ResponseGenerator handleTemplateMessage() {
        ResponseGenerator responseGenerator = new ResponseGenerator();
        switch (payload[0]) {
            case TemplateType.DBPEDIA_EXTRACTION_FRAMEWORK:
                responseGenerator.addTextResponse(new ResponseData("The DBpedia community uses a flexible and extensible framework to extract different kinds of structured information from Wikipedia. The DBpedia extraction framework is written using Scala 2.8."));
                responseGenerator.addButtonTextResponse(new ResponseData("Here are some useful links related to the the Extraction Framework:", new ArrayList<ResponseData.Button>(){{
                    add(new ResponseData.Button("GitHub", ResponseType.BUTTON_LINK, "https://github.com/dbpedia/extraction-framework"));
                    add(new ResponseData.Button("Extraction Guide", ResponseType.BUTTON_LINK, "https://github.com/dbpedia/extraction-framework/wiki/Extraction-Instructions"));
                    add(new ResponseData.Button("How to Contribute", ResponseType.BUTTON_LINK, "https://github.com/dbpedia/extraction-framework/wiki/Contributing"));
                }}));

                break;
        }
        return responseGenerator;
    }
}
