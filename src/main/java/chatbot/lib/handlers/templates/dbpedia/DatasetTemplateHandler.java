package chatbot.lib.handlers.templates.dbpedia;

import chatbot.Application;
import chatbot.lib.handlers.TemplateHandler;
import chatbot.lib.request.Request;
import chatbot.lib.request.TemplateType;
import chatbot.lib.api.DatabusService;
import chatbot.lib.response.ResponseData;
import chatbot.lib.response.ResponseGenerator;
import chatbot.lib.response.ResponseType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ramgathreya on 7/12/17.
 */
public class DatasetTemplateHandler extends TemplateHandler {
    public DatasetTemplateHandler(Request request, String[] payload, Application.Helper helper) {
        super(request, payload, helper);
    }

    public ResponseGenerator handleTemplateMessage() {
        ResponseGenerator responseGenerator = new ResponseGenerator();
        switch (payload[0]) {
            case TemplateType.DBPEDIA_DATASET:
                responseGenerator.addTextResponse(new ResponseData(
                        "The DBpedia data set uses a large multi-domain ontology which has been derived from Wikipedia as well as  localized versions of DBpedia in more than 100 languages."));
                responseGenerator.addButtonTextResponse(new ResponseData(
                        "You can find all DBpedia dataset dumps and information about them at the following links:",
                        new ArrayList<ResponseData.Button>() {
                            {
                                add(new ResponseData.Button("Background Information", ResponseType.BUTTON_LINK,
                                        "http://wiki.dbpedia.org/services-resources/datasets/dbpedia-datasets"));
                                add(new ResponseData.Button("All Dataset Dumps", ResponseType.BUTTON_LINK,
                                        "http://wiki.dbpedia.org/datasets"));
                            }
                        }));
                break;
            case TemplateType.DBPEDIA_DATASET_NLP:
                responseGenerator.addTextResponse(new ResponseData(
                        "The NLP Datasets were created by the DBpedia Spotlight team to support entity recognition and disambiguation tasks, among others. "));
                responseGenerator.addButtonTextResponse(
                        new ResponseData("Here is the link to the dataset:", new ArrayList<ResponseData.Button>() {
                            {
                                add(new ResponseData.Button("NLP Dataset", ResponseType.BUTTON_LINK,
                                        "http://wiki.dbpedia.org/services-resources/datasets/nlp"));
                            }
                        }));
                break;
            case TemplateType.DBPEDIA_DATASET_ONTOLOGY:
                responseGenerator.addButtonTextResponse(new ResponseData(
                        "The DBpedia Ontology is a shallow, cross-domain ontology, which has been manually created based on the most commonly used infoboxes within Wikipedia. The ontology currently covers 685 classes which form a subsumption hierarchy and are described by 2,795 different properties. Here is the link to the dataset:",
                        new ArrayList<ResponseData.Button>() {
                            {
                                add(new ResponseData.Button("Ontology Dataset", ResponseType.BUTTON_LINK,
                                        "http://wiki.dbpedia.org/services-resources/ontology"));
                            }
                        }));
                break;
            case TemplateType.DBPEDIA_DATABUS_RECOMMENDATION:
                if (payload.length > 1) {
                    String query = payload[1];
                    List<ResponseData> results = new DatabusService().getRecommendedDatasets(query);
                    if (results.isEmpty()) {
                        responseGenerator.addTextResponse(new ResponseData("No datasets found regarding " + query));
                    } else {
                        responseGenerator.addTextResponse(
                                new ResponseData("Here are some recommended datasets for '" + query + "':"));
                        responseGenerator.addCarouselResponse(results);
                    }
                } else {
                    responseGenerator.addTextResponse(new ResponseData(
                            "Please specify a topic for dataset recommendation, e.g., 'recommend datasets for cars'."));
                }
                break;
        }
        return responseGenerator;
    }
}
