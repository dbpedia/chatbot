package chatbot.lib;

import static junit.framework.TestCase.assertTrue;

/**
 * Created by ramgathreya on 6/20/17.
 */
public class TestUtility {
    public static void checkUrl(String url) {
        assertTrue("Check If URL is Proper", url.matches("^(((http|https)://)|mailto:).*$"));
    }
}
