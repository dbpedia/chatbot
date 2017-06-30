package chatbot.lib.request;

import chatbot.Application;
import chatbot.couchbase.Chat;
import chatbot.couchbase.ChatRepository;
import chatbot.lib.Utility;
import chatbot.lib.handlers.ParameterHandler;
import chatbot.lib.response.Response;
import chatbot.lib.handlers.TextHandler;
import chatbot.lib.response.ResponseGenerator;
import chatbot.lib.response.ResponseType;
import chatbot.rivescript.RiveScriptBot;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

/**
 * Created by ramgathreya on 5/22/17.
 */
public class RequestRouter {
    private Request request;
    private String msgId;
    private Application.Helper helper;

    public RequestRouter(Request request, Application.Helper helper) {
        this.request = request;
        this.helper = helper;
        msgId = request.getUserId() + Chat.ID_SEPARATOR + new Date().getTime();
    }

    private void addRequestChatHistory() {
//        chatRepository.save(new Chat()
//                .setId(msgId + Chat.ID_SEPARATOR + "request")
//                .setUserId(request.getUserId())
//                .setRequest(request)
//                .setDate(new Date())
//        );
    }

    private void addResponseChatHistory(List<Response> responseList) {
//        Date date = new Date();
//        String userId = request.getUserId();
//        chatRepository.save(new Chat()
//                .setId(msgId + Chat.ID_SEPARATOR + "response")
//                .setUserId(userId)
//                .setFromBot(true)
//                .setResponse(responseList)
//                .setDate(date)
//        );
    }

    public List<Response> routeRequest() throws Exception {
        ResponseGenerator responseGenerator = new ResponseGenerator();
//        addRequestChatHistory();
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
//        addResponseChatHistory(response);
        return response;
    }
}
