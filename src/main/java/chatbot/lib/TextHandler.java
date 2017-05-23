package chatbot.lib;

import chatbot.lib.response.Response;
import chatbot.lib.response.ResponseData;
import chatbot.lib.response.ResponseGenerator;
import chatbot.rivescript.RiveScriptBot;

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

    public List<Response> handleTextMessage() {
        return new ResponseGenerator()
                .addTextResponse(new ResponseData(riveScriptBot.reply(userId, textMessage)))
                .getResponse();
    }
}
