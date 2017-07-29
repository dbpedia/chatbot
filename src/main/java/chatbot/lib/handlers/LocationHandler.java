package chatbot.lib.handlers;

import chatbot.Application;
import chatbot.lib.api.NominatimService;
import chatbot.lib.api.dbpedia.LookupService;
import chatbot.lib.api.qa.QAService;
import chatbot.lib.request.Request;
import chatbot.lib.response.ResponseData;
import chatbot.lib.response.ResponseGenerator;
import chatbot.rivescript.RiveScriptReplyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by ramgathreya on 7/26/17.
 */
public class LocationHandler {
    private static final Logger logger = LoggerFactory.getLogger(LocationHandler.class);

    private String question;
    private Request request;
    private Application.Helper helper;
    private LookupService lookupService;

    public LocationHandler(Request request, String question, Application.Helper helper) {
        this.request = request;
        this.question = question;
        this.helper = helper;
        lookupService = new LookupService();
    }

    private List<ResponseData> getResponseData(String uri) {
        List<ResponseData> responseDataList = new ArrayList<>();
        ResponseData entityData = helper.getSparql().getEntityInformation(uri);
        ResponseData data = new NominatimService().reverseGeoCode(entityData.getTitle());

        if(data != null) {
            responseDataList.add(data);
        }
        responseDataList.add(entityData);
        return responseDataList;
    }

    public ResponseGenerator getLocation() {
        ResponseGenerator responseGenerator = new ResponseGenerator();
        List<ResponseData> responseDataList = new ArrayList<>();

        String uri = lookupService.setQueryClass("place").search(question);
        // If entity has been found
        if(uri != null) {
            responseDataList = getResponseData(uri);
        }
        // When there is no direct entity match
        else {
            QAService qaService = new QAService(helper.getWolframRepository());
            QAService.Data data = qaService.search(question);
            if(data != null && data.getUris().size() > 0) {
                Iterator iterator = data.getUris().iterator();
                uri = (String) iterator.next();
                responseDataList = getResponseData(uri);

            }
        }

        if(responseDataList.size() > 0) {
            responseGenerator.addTextResponse(new ResponseData(helper.getRiveScriptBot().answer(request.getUserId(), RiveScriptReplyType.NL_ANSWER_TEXT)[0]));
            responseGenerator.addCarouselResponse(responseDataList);
        }
        return responseGenerator;
    }
}
