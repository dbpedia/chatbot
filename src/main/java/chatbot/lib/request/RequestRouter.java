package chatbot.lib.request;

import chatbot.Application;
import chatbot.couchdb.Chat;
import chatbot.lib.handlers.ParameterHandler;
import chatbot.lib.handlers.TextHandler;
import chatbot.lib.response.Response;
import chatbot.lib.response.ResponseGenerator;

import java.util.Date;
import java.util.List;

/**
 * Created by ramgathreya on 5/22/17.
 */
public class RequestRouter {
    private Request request;
    private String msgId;
    private Application.Helper helper;
    private long timestamp;

    public RequestRouter(Request request, Application.Helper helper) {
        this.request = request;
        this.helper = helper;
        timestamp = new Date().getTime();
        msgId = request.getUserId() + Chat.ID_SEPARATOR + timestamp;
    }

    private void addRequestChatHistory() {
        helper.getChatDB().save(new Chat()
                .setId(msgId + Chat.ID_SEPARATOR + "request")
                .setUserId(request.getUserId())
                .setRequest(request)
                .setTimestamp(timestamp)
        );
    }

    private void addResponseChatHistory(List<Response> responseList) {
        helper.getChatDB().save(new Chat()
                .setId(msgId + Chat.ID_SEPARATOR + "response")
                .setUserId(request.getUserId())
                .setFromBot(true)
                .setResponse(responseList)
                .setTimestamp(new Date().getTime())
        );
    }

    public List<Response> routeRequest() throws Exception {
        ResponseGenerator responseGenerator = new ResponseGenerator();
        addRequestChatHistory();
        switch(request.getMessageType()) {
            case RequestType.TEXT_MESSAGE:
                responseGenerator = new TextHandler(request, request.getText(), helper)
                    .handleTextMessage();
                break;
            case RequestType.PARAMETER_MESSAGE:
                responseGenerator = new ParameterHandler(request, request.getPayload(), helper)
                    .handleParameterMessage();
                break;
        }
        // Adding Feedback Response if required
        responseGenerator.addFeedbackResponse(msgId);

        List<Response> response = responseGenerator.getResponse();
        addResponseChatHistory(response);
        return response;
    }
}
