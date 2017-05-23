package chatbot.app.request;

import chatbot.lib.ParameterHandler;
import chatbot.lib.response.Response;
import chatbot.lib.request.RequestType;
import chatbot.lib.TextHandler;
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

    public List<Response> handleRequest() {
        List<Response> response = null;
        switch(request.getMessageType()) {
            case RequestType.TEXT_MESSAGE:
                response = new TextHandler("USER", request.getText(), riveScriptBot)
                    .handleTextMessage();
                break;
            case RequestType.PARAMETER_MESSAGE:
                response = new ParameterHandler("USER", request.getPayload())
                    .handleParameterMessage();
                break;
        }
        return response;
    }
}
