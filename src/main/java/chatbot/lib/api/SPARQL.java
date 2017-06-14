package chatbot.lib.api;

import chatbot.lib.Predicate;
import chatbot.lib.response.ResponseData;
import chatbot.lib.response.ResponseType;
import org.apache.jena.query.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by ramgathreya on 6/2/17.
 */

// https://stackoverflow.com/questions/1644252/querying-dbpedia-with-sparql-and-jena
// https://jena.apache.org/documentation/query/app_api.html

public class SPARQL {
    private static final String ENDPOINT = "https://dbpedia.org/sparql";
    private static final Logger logger = LoggerFactory.getLogger(SPARQL.class);

    private String select;
    private String where;
    private String[] prefixes = new String[] {
            "foaf: <http://xmlns.com/foaf/0.1/>",
            "rdfs: <http://www.w3.org/2000/01/rdf-schema#>",
            "dbo: <http://dbpedia.org/ontology/>",
            "dbp: <http://dbpedia.org/property/>",
            "dbr: <http://dbpedia.org/resource/>"
    };

    public String[] getPrefixes() {
        return prefixes;
    }

    public SPARQL setPrefixes(String[] prefixes) {
        List<String> strings = new ArrayList<>();
        for (String prefix: this.prefixes) {
            strings.add(prefix);
        }

        for (String prefix : prefixes) {
            strings.add(prefix);
        }

        this.prefixes = (String[]) strings.toArray();
        return this;
    }

    public String getSelect() {
        return select;
    }

    public SPARQL setSelect(String select) {
        this.select = select;
        return this;
    }

    public String getWhere() {
        return where;
    }

    public SPARQL setWhere(String where) {
        this.where = where;
        return this;
    }

    private String buildQuery() {
        String query = "";
        // Adding Prefixes
        if (prefixes != null) {
            for(String prefix : prefixes) {
                query += "PREFIX " + prefix + "\n";
            }
        }

        if (select != null) {
            query += "SELECT " + select + "\n";
        }

        if (where != null) {
            query += "WHERE {" + where + "}\n";
        }

        return query;
    }

    public ResponseData getEntityInformation(String uri) {
        QueryExecution queryExecution = setSelect("DISTINCT ?property ?value")
                .setWhere("<" + uri + "> ?property ?value. filter( (?property = rdfs:label && lang(?value) = 'en' ) || (?property = dbo:abstract && lang(?value) = 'en' ) || ?property=dbo:thumbnail || ?property=foaf:isPrimaryTopicOf) .")
                .executeQuery();
        try {
            Iterator<QuerySolution> results = queryExecution.execSelect();
            if(results != null) {
                ResponseData responseData = new ResponseData();
                while(results.hasNext()) {
                    QuerySolution result = results.next();
                    String value = result.get("value").toString()
                            .replace("\\\"", "\""); // Remove Escaping

                    switch(result.get("property").toString()) {
                        case Predicate.RDFS_LABEL:
                            // This needs to be modified when more languages are supported
                            responseData.setTitle(value.replace("@en", ""));
                            break;
                        case Predicate.DBO_THUMBNAIL:
                            responseData.setImage(value);
                            break;
                        case Predicate.DBO_ABSTRACT:
                            responseData.setText(value);
                            break;
                        case Predicate.FOAF_IS_PRIMARY_TOPIC_OF:
                            responseData.addButton(new ResponseData.ButtonData("View in Wikipedia", ResponseType.BUTTON_LINK, value));
                            break;
                        default:
                            logger.debug("PROPERTY: " + result.get("property").toString());
                            logger.debug("VALUE: " + result.get("value").toString());
                    }
                }
                responseData.addButton(new ResponseData.ButtonData("View in DBpedia", ResponseType.BUTTON_LINK, uri));
                return responseData;
            }
        }
        finally {
            queryExecution.close();
        }
        return null;
    }

    public QueryExecution executeQuery() {
        Iterator<QuerySolution> results = null;
        String queryString = buildQuery();
        logger.info("SPARQL Query is:\n" + queryString);

        Query query = QueryFactory.create(queryString);
        return QueryExecutionFactory.sparqlService(ENDPOINT, query);
    }
}
