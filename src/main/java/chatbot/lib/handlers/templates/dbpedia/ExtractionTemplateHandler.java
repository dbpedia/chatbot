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
                responseGenerator.addTextResponse(new ResponseData("DBpedia Mappings Wiki helps to enhance the information in DBpedia. The DBpedia Extraction Framework uses the mappings defined through the DBpedia Mappings tool to homogenize information extracted from Wikipedia before generating structured information in RDF."));
                responseGenerator.addButtonTextResponse(new ResponseData("Here are some useful links related to the Mappings Wiki:", new ArrayList<ResponseData.Button>(){{
                    add(new ResponseData.Button("Wiki", ResponseType.BUTTON_LINK, "http://mappings.dbpedia.org/index.php/Main_Page"));
                    add(new ResponseData.Button("Mappings Tool", ResponseType.BUTTON_LINK, "http://mappings.dbpedia.org/mappingtool/web/"));
                    add(new ResponseData.Button("Extraction Framework", ResponseType.BUTTON_PARAM, "http://mappings.dbpedia.org/mappingtool/web/"));
                }}));

                break;
        }
        return responseGenerator;
    }
}
