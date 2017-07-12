package chatbot.lib.handlers.templates.entity;

import chatbot.Application;
import chatbot.lib.Utility;
import chatbot.lib.handlers.templates.OptionsTemplateHandler;
import chatbot.lib.request.Request;
import chatbot.lib.request.TemplateType;
import chatbot.lib.response.ResponseData;
import chatbot.lib.response.ResponseGenerator;
import chatbot.rivescript.RiveScriptReplyType;
import org.slf4j.LoggerFactory;
/**
 * Created by ramgathreya on 7/3/17.
 */
public class MovieTemplateHandler extends OptionsTemplateHandler {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MovieTemplateHandler.class);

    public MovieTemplateHandler(Request request, String[] payload, Application.Helper helper) {
        super(request, payload, helper);
    }

    public ResponseData getMovieOptions(String uri, String tvId, String label) {
        ResponseData responseData = new ResponseData(helper.getRiveScriptBot().answer(request.getUserId(), RiveScriptReplyType.LEARN_MORE_TEXT + " " + label)[0]);
        responseData.addSmartReply(new ResponseData.SmartReply("Cast", TemplateType.MOVIE_CAST + Utility.STRING_SEPARATOR + uri + Utility.STRING_SEPARATOR + tvId + Utility.STRING_SEPARATOR + label));
        responseData.addSmartReply(new ResponseData.SmartReply("Crew", TemplateType.MOVIE_CREW + Utility.STRING_SEPARATOR + uri + Utility.STRING_SEPARATOR + tvId + Utility.STRING_SEPARATOR + label));
        responseData.addSmartReply(new ResponseData.SmartReply("Similar", TemplateType.MOVIE_SIMILAR + Utility.STRING_SEPARATOR + uri + Utility.STRING_SEPARATOR + tvId + Utility.STRING_SEPARATOR + label));
        responseData.addSmartReply(new ResponseData.SmartReply("Related", TemplateType.MOVIE_RELATED + Utility.STRING_SEPARATOR + uri + Utility.STRING_SEPARATOR + tvId + Utility.STRING_SEPARATOR + label));
        return responseData;
    }

    public ResponseGenerator handleTemplateMessage() {
        ResponseGenerator responseGenerator = new ResponseGenerator();
        switch(payload[0]) {
            case TemplateType.MOVIE_CAST:
            case TemplateType.MOVIE_CREW:
                responseGenerator.addResponses(getTMDBData().getResponse());
                break;
            case TemplateType.MOVIE_SIMILAR:
                responseGenerator = getSimilarOrRelatedEntities(TemplateType.LOAD_SIMILAR, payload[1]);
                break;
            case TemplateType.MOVIE_RELATED:
                responseGenerator = getSimilarOrRelatedEntities(TemplateType.LOAD_RELATED, payload[1]);
                break;
        }
        responseGenerator.addSmartReplyResponse(getMovieOptions(payload[1], payload[2], payload[3]));
        return responseGenerator;
    }

}
