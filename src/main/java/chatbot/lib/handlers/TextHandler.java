package chatbot.lib.handlers;

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

    public List<Response> handleTextMessage() throws Exception {
        ResponseGenerator responseGenerator = new ResponseGenerator();

        NLHandler nlHandler = new NLHandler(textMessage);
        List<ResponseData> responseData = nlHandler.answer();

        // Need to be customized as requrired later on
        // This needs to be returned back to texthandler probably better to have all response processing there
        responseGenerator.addTextResponse(new ResponseData("This is what I found:"));
        responseGenerator.addCarouselResponse(responseData.toArray(new ResponseData[responseData.size()]));

        return responseGenerator.getResponse();

//        return new ResponseGenerator()
//                .addTextResponse(new ResponseData(riveScriptBot.reply(userId, textMessage)))
//                .getResponse();
    }
}
