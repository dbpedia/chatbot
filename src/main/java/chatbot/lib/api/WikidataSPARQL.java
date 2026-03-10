package chatbot.lib.api;

import chatbot.lib.Constants;
import chatbot.lib.Utility;
import chatbot.lib.request.TemplateType;
import chatbot.lib.response.ResponseData;
import chatbot.lib.response.ResponseType;
import org.apache.jena.query.*;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Service for querying the Wikidata SPARQL endpoint to retrieve entity
 * information.
 * Mirrors the interface of the DBpedia SPARQL class but targets Wikidata.
 */
public class WikidataSPARQL {
    private static final Logger logger = LoggerFactory.getLogger(WikidataSPARQL.class);

    private static final String ENDPOINT = "https://query.wikidata.org/sparql";
    private static final String PREFIXES = "PREFIX wd: <http://www.wikidata.org/entity/>\n" +
            "PREFIX wdt: <http://www.wikidata.org/prop/direct/>\n" +
            "PREFIX wikibase: <http://wikiba.se/ontology#>\n" +
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
            "PREFIX schema: <http://schema.org/>\n";

    public WikidataSPARQL() {
    }

    public String buildQuery(String query) {
        return PREFIXES + query;
    }

    /**
     * Retrieves entity information from Wikidata for the given URI.
     * Returns a ResponseData card with label, description, image, Wikipedia link,
     * and Wikidata link.
     */
    public ResponseData getEntityInformation(String uri) {
        String entityId = Utility.extractWikidataEntityId(uri);
        if (entityId == null) {
            return null;
        }

        String query = buildQuery(
                "SELECT ?label ?description ?image ?articleEN WHERE {\n" +
                        "  wd:" + entityId + " rdfs:label ?label . FILTER(lang(?label) = 'en') .\n" +
                        "  OPTIONAL { wd:" + entityId
                        + " schema:description ?description . FILTER(lang(?description) = 'en') }\n" +
                        "  OPTIONAL { wd:" + entityId + " wdt:P18 ?image }\n" +
                        "  OPTIONAL {\n" +
                        "    ?articleEN schema:about wd:" + entityId + " ;\n" +
                        "              schema:isPartOf <https://en.wikipedia.org/> .\n" +
                        "  }\n" +
                        "} LIMIT 1");

        QueryExecution queryExecution = null;
        ResponseData responseData = null;

        try {
            queryExecution = executeQuery(query);
            Iterator<QuerySolution> results = queryExecution.execSelect();
            if (results.hasNext()) {
                QuerySolution result = results.next();
                responseData = new ResponseData();

                // Label
                String label = result.get("label").asLiteral().getString();
                responseData.setTitle(label);

                // Description
                if (result.get("description") != null) {
                    responseData.setText(result.get("description").asLiteral().getString());
                }

                // Thumbnail / Image
                if (result.get("image") != null) {
                    responseData.setImage(result.get("image").toString());
                }

                // Wikipedia link
                if (result.get("articleEN") != null) {
                    responseData.addButton(new ResponseData.Button("View in Wikipedia", ResponseType.BUTTON_LINK,
                            result.get("articleEN").toString()));
                }

                // Wikidata link
                responseData.addButton(new ResponseData.Button("View in Wikidata", ResponseType.BUTTON_LINK, uri));

                // Learn More button (reuses the existing template mechanism)
                responseData.addButton(new ResponseData.Button("Learn More", ResponseType.BUTTON_PARAM,
                        TemplateType.LEARN_MORE + Utility.STRING_SEPARATOR + uri + Utility.STRING_SEPARATOR + label));
            }
        } catch (Exception e) {
            logger.error("Error querying Wikidata for entity: " + uri, e);
        } finally {
            if (queryExecution != null) {
                queryExecution.close();
            }
        }
        return responseData;
    }

    /**
     * Retrieves the English label for a Wikidata entity.
     */
    public String getLabel(String uri) {
        String entityId = Utility.extractWikidataEntityId(uri);
        if (entityId == null)
            return null;

        String query = buildQuery(
                "SELECT ?label WHERE {\n" +
                        "  wd:" + entityId + " rdfs:label ?label . FILTER(lang(?label) = 'en') .\n" +
                        "} LIMIT 1");

        QueryExecution queryExecution = executeQuery(query);
        String label = null;

        try {
            Iterator<QuerySolution> results = queryExecution.execSelect();
            if (results.hasNext()) {
                label = results.next().get("label").asLiteral().getString();
            }
        }
        catch (Exception e) {
            logger.error("Error querying Wikidata for label: " + uri, e);
        }
        finally {
            if (queryExecution != null) {
                queryExecution.close();
            }
        }
        return label;
    }

    /**
     * Executes a SPARQL query against the Wikidata endpoint.
     */
    public QueryExecution executeQuery(String queryString) {
        logger.info("Wikidata SPARQL Query is:\n" + queryString);
        Query query = QueryFactory.create(queryString);
        QueryEngineHTTP queryEngine = (QueryEngineHTTP) QueryExecutionFactory.sparqlService(ENDPOINT, query);
        queryEngine.addParam("timeout", String.valueOf(Constants.API_TIMEOUT));
        // Wikidata requires a User-Agent header
        queryEngine.addParam("format", "json");
        return queryEngine;
    }
}
