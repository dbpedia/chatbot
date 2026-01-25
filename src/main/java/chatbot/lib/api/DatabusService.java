package chatbot.lib.api;

import chatbot.lib.Constants;
import chatbot.lib.response.ResponseData;
import chatbot.lib.response.ResponseType;
import org.apache.jena.query.*;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Service to query Databus for datasets.
 */
public class DatabusService {
    private static final Logger logger = LoggerFactory.getLogger(DatabusService.class);
    private static final String ENDPOINT = "https://databus.dbpedia.org/repo/sparql";

    private static final String PREFIXES = new String(
            "PREFIX dataid: <http://dataid.dbpedia.org/ns/core#>\n" +
                    "PREFIX dct: <http://purl.org/dc/terms/>\n" +
                    "PREFIX dcat: <http://www.w3.org/ns/dcat#>\n" +
                    "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n");

    public String buildQuery(String query) {
        return PREFIXES + query;
    }

    public List<ResponseData> getRecommendedDatasets(String keyword) {
        List<ResponseData> responseDataList = new ArrayList<>();
        String sparqlQuery = buildQuery(
                "SELECT DISTINCT ?dataset ?label ?description WHERE {\n" +
                        "  ?dataset a dataid:Group .\n" +
                        "  ?dataset rdfs:label ?label .\n" +
                        "  ?dataset dct:description ?description .\n" +
                        "  FILTER (regex(?label, \"" + keyword + "\", \"i\") || regex(?description, \"" + keyword
                        + "\", \"i\"))\n" +
                        "} LIMIT 5");

        logger.info("Querying Databus: " + sparqlQuery);

        Query query = QueryFactory.create(sparqlQuery);
        try (QueryEngineHTTP queryEngine = (QueryEngineHTTP) QueryExecutionFactory.sparqlService(ENDPOINT, query)) {
            queryEngine.addParam("timeout", String.valueOf(Constants.API_TIMEOUT));
            ResultSet results = queryEngine.execSelect();
            while (results.hasNext()) {
                QuerySolution solution = results.next();
                ResponseData data = new ResponseData();

                String label = "";
                if (solution.contains("label") && solution.get("label").isLiteral()) {
                    label = solution.getLiteral("label").getString();
                }

                String description = "";
                if (solution.contains("description") && solution.get("description").isLiteral()) {
                    description = solution.getLiteral("description").getString();
                }

                data.setTitle(label.isEmpty() ? "Unknown Dataset" : label);
                if (!description.isEmpty()) {
                    // Truncate description if too long
                    if (description.length() > 200) {
                        description = description.substring(0, 197) + "...";
                    }
                    data.setText(description);
                }

                if (solution.contains("dataset") && solution.get("dataset").isURIResource()) {
                    data.addButton(new ResponseData.Button("View on Databus", ResponseType.BUTTON_LINK,
                            solution.getResource("dataset").getURI()));
                }

                responseDataList.add(data);
            }
        } catch (Exception e) {
            logger.error("Error querying Databus", e);
        }
        return responseDataList;
    }
}
