package chatbot.lib.handlers;

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

    @Test
    public void testLiteral() throws Exception { // Very basic test case which can be improved upon later
        RiveScriptBot riveScriptBot = new RiveScriptBot();
        String userId = "user";
        String question = "What is the population of France?";

        NLHandler nlHandler = new NLHandler(userId, question, riveScriptBot);
        List<Response> response = nlHandler.answer();

        assertNotNull(response);
        assertEquals(response.size(), 1);
        assertEquals(response.get(0).getMessageType(), ResponseType.TEXT_MESSAGE);
        assertNotNull(response.get(0).getMessageData().get(0).getText());
    }
}
