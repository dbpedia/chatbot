package rivescript;

import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by ramgathreya on 7/14/17.
 */
public class TestBot extends RiveScriptBase {
    @Test
    public void testBotName() {
        String[] testCases = new String[]{"Hi what is your name", "Hi, what is your name"};
        String[] expectedAnswer = new String[]{"I am the DBpedia Bot"};
        checkAnswers(testCases, expectedAnswer, true);
    }
}
