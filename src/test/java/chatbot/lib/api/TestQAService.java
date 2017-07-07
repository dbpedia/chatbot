package chatbot.lib.api;

import chatbot.cache.WolframRepository;
import chatbot.lib.TestUtility;
import chatbot.lib.api.qa.QAService;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;

/**
 * Created by ramgathreya on 6/21/17.
 */
public class TestQAService {
    @Test
    public void checkService() throws Exception {
        assertNotNull(new QAService(TestUtility.getHelper().getWolframRepository()).search("Barack Obama"));
    }
}
