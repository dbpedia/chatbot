package chatbot.lib.api.dbpedia;

import chatbot.cache.WolframRepository;
import chatbot.lib.TestUtility;
import chatbot.lib.api.dbpedia.GenesisService;
import chatbot.lib.api.qa.QAService;
import org.junit.Test;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by ramgathreya on 6/21/17.
 */
public class TestGenesisService {
    @Test
    public void checkSimilar() throws Exception {
        String uris = new GenesisService(0).getSimilarEntities("http://dbpedia.org/resource/Barack_Obama");
        assertNotNull(uris);
        assertNotEquals(uris.trim(), "");
    }

    @Test
    public void checkRelated() throws Exception {
        String uris = new GenesisService(0).getRelatedEntities("http://dbpedia.org/resource/Barack_Obama");
        assertNotNull(uris);
        assertNotEquals(uris.trim(), "");
    }
}
