package chatbot.lib.handlers;

import chatbot.lib.response.Response;
import chatbot.lib.response.ResponseData;
import chatbot.lib.response.ResponseGenerator;
import chatbot.rivescript.RiveScriptBot;
import chatbot.rivescript.RiveScriptScenario;

import java.util.List;

/**
 * Created by ramgathreya on 5/22/17.
 */
public class TextHandler {
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
        NLHandler nlHandler = new NLHandler(textMessage);
        List<ResponseData> responseData = nlHandler.answer();
        String[] rivescriptReply = riveScriptBot.answer(userId, textMessage);

        // When replies are more than one then the bot was unable to adequately answer and other methods may be needed
        // to answer
        if(rivescriptReply.length > 1) {
            switch(rivescriptReply[1]) {
                case RiveScriptScenario.FALLBACK:
                    responseGenerator.addTextResponse(new ResponseData("This is what I found:"));
                    responseGenerator.addCarouselResponse(responseData.toArray(new ResponseData[responseData.size()]));
                    break;
                case RiveScriptScenario.TEMPLATE:
//                    responseGenerator.addTextResponse(new ResponseData(rivescriptReply[0]));
                    break;
            }
        }
        else {
            responseGenerator.addTextResponse(new ResponseData(rivescriptReply[0]));
        }

        return responseGenerator.getResponse();
    }
}
