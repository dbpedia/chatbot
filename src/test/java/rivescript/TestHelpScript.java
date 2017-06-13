package rivescript;

import org.junit.Test;

/**
 * Created by ramgathreya on 6/13/17.
 */
public class TestHelpScript extends TestRiveScriptBase {
    @Test
    public void testHelp() {
        String[] testCases = new String[]{"help", "start over", "what can you do", "tell me what you can do", "how can you help me", "how you can help me"};
        String[] expectedAnswer = new String[]{"{\"type\": \"template\", \"name\": \"help\"}"};
        checkAnswers(testCases, expectedAnswer, true);
    }

}
