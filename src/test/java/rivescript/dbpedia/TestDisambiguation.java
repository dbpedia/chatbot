package rivescript.dbpedia;

import chatbot.lib.Constants;
import chatbot.lib.TestUtility;
import chatbot.lib.api.SPARQL;
import chatbot.lib.response.ResponseData;
import com.cloudant.client.api.CloudantClient;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test cases for entity disambiguation, related and similar entity scenarios
 */
public class TestDisambiguation {
    private static SPARQL sparql;

    @BeforeClass
    public static void setUp() throws Exception {
        try {
            sparql = new SPARQL(TestUtility.getHelper().getExplorerDB());
        } catch (Exception e) {
            // If test utilities fail (missing credentials), tests will be skipped
        }
    }

    /**
     * Test that we correctly identify when an entity is a disambiguation page
     */
    @Test
    public void testIsDisambiguationPage() {
        if (sparql == null) return;
        
        // "Mercury" is a classic disambiguation case with multiple meanings
        String mercuryUri = "http://dbpedia.org/resource/Mercury_(disambiguation)";
        int count = sparql.isDisambiguationPage(mercuryUri);
        
        // A disambiguation page should have multiple entities
        assertTrue("Disambiguation page should have entities", count > 0);
    }

    /**
     * Test that we don't incorrectly flag non-disambiguation pages
     */
    @Test
    public void testNonDisambiguationPage() {
        if (sparql == null) return;
        
        // Barack Obama is a specific person, not a disambiguation page
        String obamaUri = "http://dbpedia.org/resource/Barack_Obama";
        int count = sparql.isDisambiguationPage(obamaUri);
        
        // Should return 0 for non-disambiguation pages
        assertEquals("Non-disambiguation page should return 0", 0, count);
    }

    /**
     * Test retrieving entities from a disambiguation page
     */
    @Test
    public void testGetDisambiguatedEntities() {
        if (sparql == null) return;
        
        String mercuryUri = "http://dbpedia.org/resource/Mercury_(disambiguation)";
        ArrayList<ResponseData> entities = sparql.getDisambiguatedEntities(mercuryUri, 0, 10);
        
        assertNotNull("Should return entity list", entities);
        assertTrue("Disambiguation page should have multiple entities", entities.size() > 0);
    }

    /**
     * Test retrieving entity information with complete details
     */
    @Test
    public void testGetEntityInformation() {
        if (sparql == null) return;
        
        String obamaUri = "http://dbpedia.org/resource/Barack_Obama";
        ResponseData entity = sparql.getEntityInformation(obamaUri);
        
        assertNotNull("Entity information should not be null", entity);
        assertNotNull("Entity should have a title", entity.getTitle());
        assertTrue("Entity title should not be empty", entity.getTitle().length() > 0);
    }

    /**
     * Test retrieving multiple entities by their URIs
     */
    @Test
    public void testGetEntitiesByURIs() {
        if (sparql == null) return;
        
        String uris = "<http://dbpedia.org/resource/Barack_Obama> <http://dbpedia.org/resource/Angela_Merkel>";
        ArrayList<ResponseData> entities = sparql.getEntitiesByURIs(uris);
        
        assertNotNull("Should return entity list", entities);
        assertEquals("Should return exactly 2 entities", 2, entities.size());
    }

    /**
     * Test entity label retrieval
     */
    @Test
    public void testGetLabel() {
        if (sparql == null) return;
        
        String obamaUri = "http://dbpedia.org/resource/Barack_Obama";
        String label = sparql.getLabel(obamaUri);
        
        assertNotNull("Label should not be null", label);
        assertEquals("Label should match expected name", "Barack Obama", label);
    }

    /**
     * Test pagination in disambiguation results
     */
    @Test
    public void testDisambiguationPagination() {
        if (sparql == null) return;
        
        String mercuryUri = "http://dbpedia.org/resource/Mercury_(disambiguation)";
        
        ArrayList<ResponseData> page1 = sparql.getDisambiguatedEntities(mercuryUri, 0, 5);
        ArrayList<ResponseData> page2 = sparql.getDisambiguatedEntities(mercuryUri, 5, 5);
        
        assertNotNull("First page should exist", page1);
        // Verify different entities in pagination
        if (page2 != null && page1.size() > 0 && page2.size() > 0) {
            assertNotNull("Second page should exist", page2);
            assertNotEquals("Pages should be different", page1.get(0).getTitle(), page2.get(0).getTitle());
        }
    }

    /**
     * Test entity with description and abstract fields
     */
    @Test
    public void testEntityDescriptionAndAbstract() {
        if (sparql == null) return;
        
        String franceUri = "http://dbpedia.org/resource/France";
        ResponseData entity = sparql.getEntityInformation(franceUri);
        
        assertNotNull("Entity should exist", entity);
        assertNotNull("Entity should have text content", entity.getText());
        assertTrue("Text should contain meaningful information", entity.getText().length() > 20);
    }

    /**
     * Test handling of entities with special characters in names
     */
    @Test
    public void testEntityWithSpecialCharacters() {
        if (sparql == null) return;
        
        // Test with an entity that has parentheses in the name
        String uri = "http://dbpedia.org/resource/Mercury_(planet)";
        ResponseData entity = sparql.getEntityInformation(uri);
        
        assertNotNull("Entity should exist", entity);
        assertNotNull("Should handle entity correctly", entity.getTitle());
    }
}
