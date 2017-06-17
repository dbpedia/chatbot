package chatbot.lib.handlers;

import chatbot.lib.request.Request;
import chatbot.lib.response.Response;
import chatbot.lib.response.ResponseType;
import chatbot.rivescript.RiveScriptBot;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by ramgathreya on 6/13/17.
 */
public class TestNLHandler {
    private void checkLiteral(String userId, String question) throws Exception {
        RiveScriptBot riveScriptBot = new RiveScriptBot();
        NLHandler nlHandler = new NLHandler(new Request().setUserId(userId), question, riveScriptBot);
        List<Response> response = nlHandler.answer();

        assertNotNull(response);
        assertEquals(response.size(), 1);
        assertEquals(response.get(0).getMessageType(), ResponseType.TEXT_MESSAGE);
        assertNotNull(response.get(0).getMessageData().get(0).getText());
    }

    @Test
    public void testLiteral() throws Exception {
        checkLiteral("user", "What is the population of France?");
    }

}
