package rivescript;

import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by ramgathreya on 6/14/17.
 */
public class TestFallback extends TestRiveScriptBase{
    @Test
    public void testFallback() {
        String[] testCases = new String[]{"tell me about barack obama", "can you tell me about barack obama"};
        ArrayList<String[]> expectedAnswer = new ArrayList<String[]>(){{
            add(new String[]{"{\"type\": \"fallback\", \"query\": \"barack obama\"}"});
            add(new String[]{"{\"type\": \"fallback\", \"query\": \"barack obama\"}"});
        }};

        checkAnswers(testCases, expectedAnswer, true);
    }
}
