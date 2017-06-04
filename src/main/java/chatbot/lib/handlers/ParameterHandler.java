package chatbot.lib.handlers;

import chatbot.lib.Utility;
import chatbot.lib.request.ParameterType;
import chatbot.lib.response.Response;
import chatbot.lib.response.ResponseData;
import chatbot.lib.response.ResponseGenerator;
import chatbot.lib.response.ResponseTemplates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by ramgathreya on 5/23/17.
 */
public class ParameterHandler {
    private static final Logger logger = LoggerFactory.getLogger(ParameterHandler.class);
    private String userId;
    private String[] payload;

    public ParameterHandler(String userId, String payload) {
        this.userId = userId;
        this.payload = Utility.split(payload);
    }

    public List<Response> handleParameterMessage() {
        ResponseGenerator response = new ResponseGenerator();
        switch(this.payload[0]) {
            case ParameterType.START:
                response.addTextResponse(new ResponseData("Hi I am the DBpedia Bot"))
                        .addTextResponse(new ResponseData("You can ask me questions like:"))
                        .addCarouselResponse(ResponseTemplates.getStarterTemplate());
                break;
        }
        return response.getResponse();
    }
}
