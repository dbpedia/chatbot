package chatbot.lib.handlers;

import chatbot.lib.request.Request;
import chatbot.lib.response.Response;
import chatbot.lib.response.TestResponseBase;
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
        List<Response> response = nlHandler.answer().getResponse();

        assertNotNull(response);
        assertEquals(response.size(), 1);
        TestResponseBase.checkTextMessage(response.get(0));
    }

    private void checkEntity(String userId, String question) throws Exception {
        RiveScriptBot riveScriptBot = new RiveScriptBot();
        NLHandler nlHandler = new NLHandler(new Request().setUserId(userId), question, riveScriptBot);
        List<Response> response = nlHandler.answer().getResponse();
        assertEquals(response.size(), 2);
        TestResponseBase.checkTextMessage(response.get(0));
        assertEquals(response.get(1).getMessageData().size(), 1); // Check that Carousel contains only 1 element
        TestResponseBase.checkCarouselMessage(response.get(1));
    }

    @Test
    public void testLiteral() throws Exception {
        checkLiteral("user", "What is the population of France?");
    }

    @Test
    public void testEntity() throws Exception {
        checkEntity("user", "Barack Obama");

    }

}
