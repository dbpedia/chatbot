package chatbot.lib.handlers;

import chatbot.lib.TestUtility;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by ramgathreya on 8/1/17.
 */
public class TestTextHandler {

    @Test
    public void testSanitizeText() throws Exception {
        // Request and Helper do not matter for this test case
        assertEquals("download nlp dataset", new TextHandler(null, "download nlp dataset", null).getTextMessage());
    }
}
