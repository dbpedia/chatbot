package chatbot.lib.request;

import chatbot.lib.handlers.ParameterHandler;
import chatbot.lib.response.Response;
import chatbot.lib.handlers.TextHandler;
import chatbot.rivescript.RiveScriptBot;

import java.util.List;

/**
 * Created by ramgathreya on 5/22/17.
 */
public class RequestHandler {
    private Request request;
    private RiveScriptBot riveScriptBot;

    public RequestHandler(Request request, RiveScriptBot riveScriptBot) {
        this.request = request;
        this.riveScriptBot = riveScriptBot;
    }

    public List<Response> handleRequest() throws Exception {
        List<Response> response = null;
        switch(request.getMessageType()) {
            case RequestType.TEXT_MESSAGE:
                response = new TextHandler(request.getUserId(), request.getText(), riveScriptBot)
                    .handleTextMessage();
                break;
            case RequestType.PARAMETER_MESSAGE:
                response = new ParameterHandler(request.getUserId(), request.getPayload())
                    .handleParameterMessage();
                break;
        }
        return response;
    }
}
