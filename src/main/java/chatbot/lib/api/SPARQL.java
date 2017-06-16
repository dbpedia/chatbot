package chatbot.lib.api;

import chatbot.lib.Predicate;
import chatbot.lib.Utility;
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
    private static final String SEPARATOR = "|";
    
    private static final Logger logger = LoggerFactory.getLogger(SPARQL.class);
    private static final String PREFIXES = new String(
            "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" +
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
            "PREFIX dbo: <http://dbpedia.org/ontology/>\n" +
            "PREFIX dbp: <http://dbpedia.org/property/>\n" +
            "PREFIX dbr: <http://dbpedia.org/resource/>\n"
    );

    private static final String BASIC_ENTITY_PROPERTIES = "filter( (?property = rdfs:label && lang(?value) = 'en' ) || (?property = dbo:abstract && lang(?value) = 'en' ) || ?property=dbo:thumbnail || ?property=foaf:isPrimaryTopicOf) .";

    public static String buildQuery(String query) {
        return PREFIXES + query;
    }

    private ResponseData processEntityInformation(String uri, QuerySolution result) {
        ResponseData responseData = new ResponseData();
        String[] properties = Utility.split(result.get("properties").asLiteral().getString(), SEPARATOR);
        String[] values = Utility.split(result.get("values").asLiteral().getString(), SEPARATOR);

        for (int i = 0; i < properties.length; i++) {
            switch(properties[i]) {
                case Predicate.RDFS_LABEL:
                    // This needs to be modified when more languages are supported
                    responseData.setTitle(values[i].replace("@en", ""));
                    break;
                case Predicate.DBO_THUMBNAIL:
                    responseData.setImage(values[i]);
                    break;
                case Predicate.DBO_ABSTRACT:
                    responseData.setText(values[i]);
                    break;
                case Predicate.FOAF_IS_PRIMARY_TOPIC_OF:
                    responseData.addButton(new ResponseData.ButtonData("View in Wikipedia", ResponseType.BUTTON_LINK, values[i]));
                    break;
                default:
                    logger.debug("PROPERTY: " + properties[i]);
                    logger.debug("VALUE: " + values[i]);
            }
        }
        responseData.addButton(new ResponseData.ButtonData("View in DBpedia", ResponseType.BUTTON_LINK, uri));
        return responseData;
    }

    public ResponseData getEntityInformation(String uri) {
         String query = buildQuery(String.format("SELECT (group_concat(?property; separator='%s') as ?properties) (group_concat(?value; separator='%s') as ?values) WHERE {" +
                 "<%s> ?property ?value. " +
                 BASIC_ENTITY_PROPERTIES +
                 "}", SEPARATOR, SEPARATOR, uri));
        QueryExecution queryExecution = executeQuery(query);
        ResponseData responseData = null;

        try {
            Iterator<QuerySolution> results = queryExecution.execSelect();
            while(results.hasNext()) {
                QuerySolution result = results.next();
                responseData = processEntityInformation(uri, result);
            }
        }
        finally {
            queryExecution.close();
        }
        return responseData;
    }

    /**
     * Returns 0 if it is not a disambiguation page. Returns count otherwise
     * We can only show 10 at a time so its limited by that
     * @return 0 or count
     */
    public int isDisambiguationPage(String uri) {
         String query = buildQuery("SELECT (count(*) as ?count) WHERE {" +
                 "<" + uri + "> <http://dbpedia.org/ontology/wikiPageDisambiguates> ?o." +
                 "}");
        QueryExecution queryExecution = executeQuery(query);
        int count = 0;

        try {
            Iterator<QuerySolution> results = queryExecution.execSelect();
            if (results != null) {
                while(results.hasNext()) {
                    QuerySolution result = results.next();
                    count = result.get("count").asLiteral().getInt();
                }
            }
        }
        finally {
            queryExecution.close();
        }
        return count;
    }

    public static String getDisambiguatedEntitiesQuery(String uri, int offset, int limit) {
        return buildQuery(String.format("SELECT ?uri ?properties ?values where {" +
            "{" +
                "SELECT ?uri (group_concat(?property; separator='%s') as ?properties) (group_concat(?value; separator='%s') as ?values) WHERE {" +
                    "?uri ?property ?value ." +
                    BASIC_ENTITY_PROPERTIES +
                    "{" +
                        "SELECT ?uri WHERE {" +
                            "<%s> <http://dbpedia.org/ontology/wikiPageDisambiguates> ?uri" +
                        "} OFFSET %d LIMIT %d" +
                    "}" +
                "} GROUP BY ?uri" +
            "}" +
        "}", SEPARATOR, SEPARATOR, uri, offset, limit));
    }

    public ArrayList<ResponseData> getDisambiguatedEntities(String uri, int offset, int limit) {
        String query = getDisambiguatedEntitiesQuery(uri, offset, limit);
        QueryExecution queryExecution = executeQuery(query);
        ArrayList<ResponseData> responseDatas = new ArrayList<>();

        try {
            Iterator<QuerySolution> results = queryExecution.execSelect();
            if (results != null) {
                while(results.hasNext()) {
                    QuerySolution result = results.next();
                    responseDatas.add(processEntityInformation(result.get("uri").toString(), result));
                }
            }
        }
        finally {
            queryExecution.close();
        }

        return responseDatas;
    }

    public QueryExecution executeQuery(String queryString) {
        logger.info("SPARQL Query is:\n" + queryString);
        Query query = QueryFactory.create(queryString);
        return QueryExecutionFactory.sparqlService(ENDPOINT, query);
    }

    public static class ProcessedResponse {
        public static final String RESPONSE_CAROUSEL = "carousel";
        public static final String RESPONSE_TEXT = "text";

        private List<ResponseData> responseData = new ArrayList<>();
        private String responseType;
        private ResponseInfo responseInfo = new ResponseInfo();

        public ResponseInfo getResponseInfo() {
            return responseInfo;
        }

        public ProcessedResponse setResponseInfo(ResponseInfo responseInfo) {
            this.responseInfo = responseInfo;
            return this;
        }

        public List<ResponseData> getResponseData() {
            return responseData;
        }

        public ProcessedResponse setResponseData(List<ResponseData> responseData) {
            this.responseData = responseData;
            return this;
        }

        public String getResponseType() {
            return responseType;
        }

        public ProcessedResponse setResponseType(String responseType) {
            this.responseType = responseType;
            return this;
        }

        public ProcessedResponse addResponseData(ResponseData responseData) {
            this.responseData.add(responseData);
            return this;
        }

        public static class ResponseInfo {
            public static final String DISAMBIGUATION_PAGE = "Disambiguation Page";
            private String queryResultType = "";

            private String uri = "";
            private int count = 0;
            private int limit = 0;
            private int offset = 0;

            public boolean hasMorePages() {
                if (offset + limit < count) {
                    return true;
                }
                return false;
            }

            public void next() {
                offset += limit;
            }

            public ArrayList<ResponseData> nextPage() {
                SPARQL sparql = new SPARQL();
                return sparql.getDisambiguatedEntities(uri, offset, limit);
            }

            public String getQueryResultType() {
                return queryResultType;
            }

            public ResponseInfo setQueryResultType(String queryResultType) {
                this.queryResultType = queryResultType;
                return this;
            }

            public int getCount() {
                return count;
            }

            public ResponseInfo setCount(int count) {
                this.count = count;
                return this;
            }

            public int getLimit() {
                return limit;
            }

            public ResponseInfo setLimit(int limit) {
                this.limit = limit;
                return this;
            }

            public int getOffset() {
                return offset;
            }

            public ResponseInfo setOffset(int offset) {
                this.offset = offset;
                return this;
            }

            public String getUri() {
                return uri;
            }

            public ResponseInfo setUri(String uri) {
                this.uri = uri;
                return this;
            }
        }
    }
}
