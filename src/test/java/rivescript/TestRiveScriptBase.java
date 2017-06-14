package rivescript;

import chatbot.rivescript.RiveScriptBot;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by ramgathreya on 6/13/17.
 */
public class TestRiveScriptBase {
    String userId = "User";
    RiveScriptBot bot = new RiveScriptBot();

    private void checkAnswer(String[] expected, String[] actual, boolean positive) {
        for (int i = 0; i < expected.length; i++) {
            if (positive) {
                assertEquals(expected[i], actual[i]);
            }
            else {
                assertNotEquals(expected[i], actual[i]);
            }
        }
    }

    public void checkAnswers(String[] testCases, String[] expected, boolean positive) {
        String[] actual;
        for(int i = 0; i < testCases.length; i++) {
            actual = bot.answer(userId, testCases[i]);
            checkAnswer(expected, actual, positive);
        }
    }

    public void checkAnswers(String[] testCases, ArrayList<String[]> expected, boolean positive) {
        String[] actual;
        for(int i = 0; i < testCases.length; i++) {
            actual = bot.answer(userId, testCases[i]);
            checkAnswer(expected.get(i), actual, positive);
        }
    }
}
