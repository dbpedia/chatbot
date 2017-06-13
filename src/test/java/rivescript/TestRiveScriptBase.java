package rivescript;

import chatbot.rivescript.RiveScriptBot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by ramgathreya on 6/13/17.
 */
public class TestRiveScriptBase {
    public static void checkAnswers(String[] testCases, String[] expected, boolean positive) {
        String userId = "User";
        RiveScriptBot bot = new RiveScriptBot();
        String[] actual;

        for (String testCase : testCases) {
            actual = bot.answer(userId, testCase);
            for (int i = 0; i < expected.length; i++) {
                if (positive) {
                    assertEquals(expected[i], actual[i]);
                }
                else {
                    assertNotEquals(expected[i], actual[i]);
                }
            }
        }
    }
}
