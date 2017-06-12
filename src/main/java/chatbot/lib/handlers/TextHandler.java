package chatbot.lib.handlers;

import chatbot.lib.Utility;
import chatbot.lib.response.Response;
import chatbot.lib.response.ResponseData;
import chatbot.lib.response.ResponseGenerator;
import chatbot.rivescript.RiveScriptBot;
import chatbot.rivescript.RiveScriptReplyType;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by ramgathreya on 5/22/17.
 */
public class TextHandler {
    private static final Logger logger = LoggerFactory.getLogger(TextHandler.class);

    private String userId;
    private String textMessage;
    private RiveScriptBot riveScriptBot;

    public TextHandler(String userId, String textMessage, RiveScriptBot riveScriptBot) {
        this.userId = userId;
        this.textMessage = textMessage;
        this.riveScriptBot = riveScriptBot;
    }

    public List<Response> handleTextMessage() throws Exception {
        ResponseGenerator responseGenerator = new ResponseGenerator();
        String[] rivescriptReply = riveScriptBot.answer(userId, textMessage);

        for(String reply : rivescriptReply) {
            if(Utility.isJSONObject(reply) == true) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(reply);

                switch (rootNode.get("type").getTextValue()) {
                    case RiveScriptReplyType.TEMPLATE:
                        List<Response> responses = new ParameterHandler(userId, rootNode.get("templateName").getTextValue(), riveScriptBot)
                                .handleParameterMessage();
                        responseGenerator.addResponses(responses);
                        break;
                    case RiveScriptReplyType.STATUS_CHECK:
                        responseGenerator.addResponses(new StatusCheckHandler(userId, rootNode.get("service").getTextValue(), riveScriptBot).handleStatusCheck());
                        break;
                    case RiveScriptReplyType.FALLBACK:
                        responseGenerator.addResponses(new NLHandler(userId, textMessage, riveScriptBot).answer());
                        break;
                }
            }
            else {
                responseGenerator.addTextResponse(new ResponseData(reply));
            }
//            switch (reply) {
//                case RiveScriptReplyType.FALLBACK:
//                    responseGenerator.addResponses(new NLHandler(userId, textMessage, riveScriptBot).answer());
//                    break;
//                case RiveScriptReplyType.TEMPLATE:
//                    break;
//            }
        }

//        switch(rivescriptReply[0]) {
//            case RiveScriptReplyType.FALLBACK:
//                return new NLHandler(userId, textMessage, riveScriptBot).answer();
//            case RiveScriptReplyType.TEMPLATE:
//                return new ParameterHandler(userId, rivescriptReply[1], riveScriptBot).handleParameterMessage();
//            case RiveScriptReplyType.STATUS_CHECK:
//                return new StatusCheckHandler(userId, rivescriptReply[1], riveScriptBot).handleStatusCheck();
//            default:
//                return responseGenerator.addTextResponses(rivescriptReply).getResponse();
//        }

        return responseGenerator.getResponse();
    }
}
