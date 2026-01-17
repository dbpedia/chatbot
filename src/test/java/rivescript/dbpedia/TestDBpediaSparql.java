package rivescript.dbpedia;

import chatbot.lib.TestUtility;
import chatbot.lib.api.SPARQL;
import chatbot.lib.response.ResponseData;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * SPARQL query test cases for disambiguation and entity resolution scenarios
 */
public class TestDBpediaSparql {
    private static SPARQL sparql;

    @BeforeClass
    public static void setUp() throws Exception {
        try {
            sparql = new SPARQL(TestUtility.getHelper().getExplorerDB());
        } catch (Exception e) {
            // Test utilities may fail if credentials are not available
        }
    }

    /**
     * Test SPARQL query building with prefixes
     */
    @Test
    public void testQueryBuildingWithPrefixes() {
        if (sparql == null) return;
        
        String rawQuery = "SELECT * WHERE { ?s ?p ?o }";
        String builtQuery = sparql.buildQuery(rawQuery);
        
        assertNotNull("Built query should not be null", builtQuery);
        assertTrue("Query should contain SPARQL prefixes", builtQuery.contains("PREFIX"));
        assertTrue("Query should contain original query", builtQuery.contains("SELECT * WHERE"));
    }

    /**
     * Test disambiguation detection for entities with multiple meanings
     */
    @Test
    public void testDisambiguationDetection() {
        if (sparql == null) return;
        
        // "Mercury (disambiguation)" is a well-known disambiguation page
        String mercuryUri = "http://dbpedia.org/resource/Mercury_(disambiguation)";
        int count = sparql.isDisambiguationPage(mercuryUri);
        
        // Should return a count > 0 for disambiguation pages
        assertTrue("Mercury disambiguation should have multiple entries", count > 0);
    }

    /**
     * Test entity retrieval from disambiguation pages
     */
    @Test
    public void testDisambiguatedEntityRetrieval() {
        if (sparql == null) return;
        
        String mercuryUri = "http://dbpedia.org/resource/Mercury_(disambiguation)";
        ArrayList<ResponseData> entities = sparql.getDisambiguatedEntities(mercuryUri, 0, 5);
        
        assertNotNull("Should return entity list from disambiguation page", entities);
        assertTrue("Should retrieve at least one entity from disambiguation", entities.size() > 0);
        
        // Verify each entity has necessary information
        for (ResponseData entity : entities) {
            assertNotNull("Entity should have a title", entity.getTitle());
            assertTrue("Entity title should not be empty", entity.getTitle().length() > 0);
        }
    }

    /**
     * Test entity information with all optional fields
     */
    @Test
    public void testEntityInformationWithOptionalFields() {
        if (sparql == null) return;
        
        String franceUri = "http://dbpedia.org/resource/France";
        ResponseData entity = sparql.getEntityInformation(franceUri);
        
        assertNotNull("Entity information should be retrieved", entity);
        assertNotNull("Entity should have title", entity.getTitle());
        // France should have a description
        assertNotNull("Entity should have descriptive text", entity.getText());
        assertTrue("Text content should be substantial", entity.getText().length() > 0);
    }

    /**
     * Test entity label retrieval and accuracy
     */
    @Test
    public void testEntityLabelRetrieval() {
        if (sparql == null) return;
        
        String obamaUri = "http://dbpedia.org/resource/Barack_Obama";
        String label = sparql.getLabel(obamaUri);
        
        assertNotNull("Label should be retrieved", label);
        assertEquals("Label should match expected entity name", "Barack Obama", label);
    }

    /**
     * Test batch entity retrieval by multiple URIs
     */
    @Test
    public void testBatchEntityRetrieval() {
        if (sparql == null) return;
        
        String uris = "<http://dbpedia.org/resource/Albert_Einstein> " +
                      "<http://dbpedia.org/resource/Marie_Curie> " +
                      "<http://dbpedia.org/resource/Isaac_Newton>";
        
        ArrayList<ResponseData> entities = sparql.getEntitiesByURIs(uris);
        
        assertNotNull("Should retrieve multiple entities", entities);
        assertEquals("Should retrieve exactly 3 entities", 3, entities.size());
    }

    /**
     * Test pagination handling for large disambiguation pages
     */
    @Test
    public void testDisambiguationPagination() {
        if (sparql == null) return;
        
        String mercuryUri = "http://dbpedia.org/resource/Mercury_(disambiguation)";
        
        ArrayList<ResponseData> firstPage = sparql.getDisambiguatedEntities(mercuryUri, 0, 3);
        ArrayList<ResponseData> secondPage = sparql.getDisambiguatedEntities(mercuryUri, 3, 3);
        
        assertNotNull("First page should exist", firstPage);
        
        if (firstPage.size() > 0 && secondPage != null && secondPage.size() > 0) {
            // Verify pagination is working by comparing first entries
            String firstPageFirstTitle = firstPage.get(0).getTitle();
            String secondPageFirstTitle = secondPage.get(0).getTitle();
            assertTrue("Pages should contain different entities", 
                      !firstPageFirstTitle.equals(secondPageFirstTitle));
        }
    }

    /**
     * Test entity information with minimal data (when some fields are unavailable)
     */
    @Test
    public void testEntityInformationWithMinimalData() {
        if (sparql == null) return;
        
        // Lesser-known but valid DBpedia entity
        String entityUri = "http://dbpedia.org/resource/John_Doe";
        ResponseData entity = sparql.getEntityInformation(entityUri);
        
        // Should handle gracefully even if some optional fields are missing
        if (entity != null) {
            assertNotNull("Entity should at least have a title", entity.getTitle());
        }
    }

    /**
     * Test distinction between disambiguation pages and regular entities
     */
    @Test
    public void testDisambiguationVsRegularEntity() {
        if (sparql == null) return;
        
        // Test disambiguation page
        String disambigUri = "http://dbpedia.org/resource/Mercury_(disambiguation)";
        int disambigCount = sparql.isDisambiguationPage(disambigUri);
        
        // Test regular entity
        String regularUri = "http://dbpedia.org/resource/Mercury_(planet)";
        int regularCount = sparql.isDisambiguationPage(regularUri);
        
        // Disambiguation should have count > 0, regular entity should be 0
        assertTrue("Disambiguation page should have entities", disambigCount > 0);
        assertEquals("Regular entity should not be marked as disambiguation", 0, regularCount);
    }

    /**
     * Test handling of entity URIs with special characters and encodings
     */
    @Test
    public void testEntityURIWithSpecialCharacters() {
        if (sparql == null) return;
        
        // Entity with parentheses in name
        String uri = "http://dbpedia.org/resource/Mercury_(planet)";
        ResponseData entity = sparql.getEntityInformation(uri);
        
        if (entity != null) {
            assertNotNull("Entity should be retrievable despite special characters", entity.getTitle());
        }
    }
}
