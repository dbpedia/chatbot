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
        System.out.println(new TextHandler(null, "download nlp dataset", TestUtility.getHelper()).getTextMessage());
        assertEquals("download nlp dataset", new TextHandler(null, "download nlp dataset", TestUtility.getHelper()).getTextMessage());
    }
}
