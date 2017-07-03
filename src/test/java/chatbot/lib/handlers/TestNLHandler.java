package chatbot.lib.handlers;

import chatbot.Application;
import chatbot.cache.WolframRepository;
import chatbot.lib.request.Request;
import chatbot.lib.response.Response;
import chatbot.lib.response.TestResponseBase;
import chatbot.rivescript.RiveScriptBot;
import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by ramgathreya on 6/13/17.
 */
public class TestNLHandler {
    CloudantClient cloudantClient;

    // These need to be standardized based on properties file
    public TestNLHandler() throws MalformedURLException {
        cloudantClient = ClientBuilder.url(new URL("http://localhost:9081"))
                .username("ramgathreya")
                .password("password")
                .build();
    }

    // Proper values need to be provided here
    private void checkLiteral(String userId, String question) throws Exception {
        Application.Helper helper = new Application.Helper(cloudantClient, new WolframRepository(""), "", "", "");
        NLHandler nlHandler = new NLHandler(new Request().setUserId(userId), question, helper);
        List<Response> response = nlHandler.answer().getResponse();

        assertNotNull(response);
        assertEquals(response.size(), 1);
        TestResponseBase.checkTextMessage(response.get(0));
    }

    private void checkEntity(String userId, String question) throws Exception {
        Application.Helper helper = new Application.Helper(cloudantClient, new WolframRepository(""), "", "", "");
        NLHandler nlHandler = new NLHandler(new Request().setUserId(userId), question, helper);
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
