package chatbot.lib.handlers;

import chatbot.lib.TestUtility;
import chatbot.lib.request.Request;
import chatbot.lib.response.Response;
import chatbot.lib.response.TestResponseBase;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by ramgathreya on 8/11/17.
 */
public class TestLocationHandler {

    private Response checkPlace(String query) throws Exception {
        TextHandler textHandler = new TextHandler(new Request().setUserId("user"), query, TestUtility.getHelper());
        List<Response> responses = textHandler.handleTextMessage().getResponse();
        TestResponseBase.checkTextMessage(responses.get(0));
        Response locationResponse = responses.get(1);
        TestResponseBase.checkPlaceMessage(locationResponse);
        return locationResponse;
    }

    @Test
    public void testEiffelTower() throws Exception {
        Response locationResponse = checkPlace("where is Eiffel Tower?");
    }
}
