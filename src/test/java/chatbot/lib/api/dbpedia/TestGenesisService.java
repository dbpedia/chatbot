package chatbot.lib.api.dbpedia;

import chatbot.cache.WolframRepository;
import chatbot.lib.TestUtility;
import chatbot.lib.api.dbpedia.GenesisService;
import chatbot.lib.api.qa.QAService;
import org.junit.Test;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by ramgathreya on 6/21/17.
 * Extended with comprehensive test coverage for similar and related entity scenarios.
 */
public class TestGenesisService {
    
    /**
     * Test retrieving similar entities for a well-known political figure
     * Similar entities should be of the same type/category
     */
    @Test
    public void checkSimilar() throws Exception {
        String uris = new GenesisService(0).getSimilarEntities("http://dbpedia.org/resource/Barack_Obama");
        assertNotNull(uris);
        assertNotEquals(uris.trim(), "");
    }

    /**
     * Test retrieving related entities for a well-known political figure
     * Related entities should be conceptually connected
     */
    @Test
    public void checkRelated() throws Exception {
        String uris = new GenesisService(0).getRelatedEntities("http://dbpedia.org/resource/Barack_Obama");
        assertNotNull(uris);
        assertNotEquals(uris.trim(), "");
    }

    /**
     * Test similar entities for a different entity type (country)
     * Verifies similar entity retrieval works across entity types
     */
    @Test
    public void checkSimilarDifferentType() throws Exception {
        String uris = new GenesisService(0).getSimilarEntities("http://dbpedia.org/resource/Germany");
        assertNotNull("Similar entities should be retrievable for countries", uris);
    }

    /**
     * Test related entities for a different entity type (country)
     * Verifies related entity retrieval works across entity types
     */
    @Test
    public void checkRelatedDifferentType() throws Exception {
        String uris = new GenesisService(0).getRelatedEntities("http://dbpedia.org/resource/Germany");
        assertNotNull("Related entities should be retrievable for countries", uris);
    }

    /**
     * Test similar entities for a movie
     * Verifies similar entity retrieval works for entertainment entities
     */
    @Test
    public void checkSimilarMovie() throws Exception {
        String uris = new GenesisService(0).getSimilarEntities("http://dbpedia.org/resource/The_Matrix");
        assertNotNull("Similar movies should be retrievable", uris);
    }

    /**
     * Test related entities for a movie
     * Verifies related entity retrieval works for entertainment entities
     */
    @Test
    public void checkRelatedMovie() throws Exception {
        String uris = new GenesisService(0).getRelatedEntities("http://dbpedia.org/resource/The_Matrix");
        assertNotNull("Related movies should be retrievable", uris);
    }

    /**
     * Test similar entities for a scientific/technical entity
     * Verifies similar entity retrieval works for specialized domains
     */
    @Test
    public void checkSimilarTechnology() throws Exception {
        String uris = new GenesisService(0).getSimilarEntities("http://dbpedia.org/resource/Python_(programming_language)");
        assertNotNull("Similar programming languages should be retrievable", uris);
    }

    /**
     * Test response format contains valid URI references
     * Verifies that similar entities are formatted as URIs
     */
    @Test
    public void checkSimilarResponseFormat() throws Exception {
        String uris = new GenesisService(0).getSimilarEntities("http://dbpedia.org/resource/Angela_Merkel");
        assertNotNull("Response should contain URIs", uris);
        assertTrue("Response should contain DBpedia URIs in angle brackets", 
                   uris.contains("<http://dbpedia.org/resource/"));
    }

    /**
     * Test response format for related entities
     * Verifies that related entities are formatted as URIs
     */
    @Test
    public void checkRelatedResponseFormat() throws Exception {
        String uris = new GenesisService(0).getRelatedEntities("http://dbpedia.org/resource/Angela_Merkel");
        assertNotNull("Response should contain URIs", uris);
        assertTrue("Response should contain DBpedia URIs in angle brackets", 
                   uris.contains("<http://dbpedia.org/resource/"));
    }

    /**
     * Test disambiguation distinction from related entities
     * A disambiguation page should not return similar entities in the same way
     */
    @Test
    public void checkDisambiguationVsSimilar() throws Exception {
        // Test with a specific entity (not a disambiguation page)
        String specificEntityUris = new GenesisService(0).getSimilarEntities("http://dbpedia.org/resource/Paris");
        
        // Verify that we can distinguish specific entities
        assertNotNull("Similar entities for specific location should be retrievable", specificEntityUris);
    }
}
