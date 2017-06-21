package chatbot.lib.api;

import chatbot.lib.Utility;
import chatbot.lib.request.ParameterType;
import chatbot.lib.response.ResponseData;
import chatbot.lib.response.ResponseType;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.RDFNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by ramgathreya on 6/2/17.
 */

// https://stackoverflow.com/questions/1644252/querying-dbpedia-with-sparql-and-jena
// https://jena.apache.org/documentation/query/app_api.html
// http://tutorial-academy.com/apache-jena-tdb-crud-operations/

public class SPARQL {
    private static final String ENDPOINT = "https://dbpedia.org/sparql";
    private static final String SEPARATOR = "|";

    // Variables used in SPARQL Queries
    private static final String VAR_URI = "uri";
    private static final String VAR_LABEL = "label";
    private static final String VAR_THUMBNAIL = "thumbnail";
    private static final String VAR_ABSTRACT = "abstract";
    private static final String VAR_PRIMARY_TOPIC = "primaryTopic";

    private static final Logger logger = LoggerFactory.getLogger(SPARQL.class);
    private static final String PREFIXES = new String(
            "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" +
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
            "PREFIX dbo: <http://dbpedia.org/ontology/>\n" +
            "PREFIX dbp: <http://dbpedia.org/property/>\n" +
            "PREFIX dbr: <http://dbpedia.org/resource/>\n"
    );

    private static final String BASIC_ENTITY_PROPERTIES = "filter( (?property = rdfs:label && lang(?value) = 'en' ) || (?property = dbo:abstract && lang(?value) = 'en' ) || ?property=dbo:thumbnail || ?property=foaf:isPrimaryTopicOf) .";

    public String buildQuery(String query) {
        return PREFIXES + query;
    }

    private ResponseData processEntityInformation(String uri, QuerySolution result) {
        RDFNode node;
        ResponseData responseData = new ResponseData();
        responseData.setTitle(result.get(VAR_LABEL).asLiteral().getString());
        responseData.addButton(new ResponseData.ButtonData("View in Wikipedia", ResponseType.BUTTON_LINK, result.get(VAR_PRIMARY_TOPIC).toString()));

        node = result.get(VAR_THUMBNAIL);
        if(node != null) {
            responseData.setImage(node.toString());
        }

        // responseData.setText(new GenesisService().getSummary(uri));

       node = result.get(VAR_ABSTRACT);
       if(node != null) {
           responseData.setText(node.asLiteral().getString());
       }
        responseData.addButton(new ResponseData.ButtonData("View in DBpedia", ResponseType.BUTTON_LINK, uri));
        responseData.addButton(new ResponseData.ButtonData("Similar", ResponseType.BUTTON_PARAM, ParameterType.LOAD_SIMILAR + Utility.STRING_SEPARATOR + uri));
        return responseData;
    }

    public ResponseData getEntityInformation(String uri) {
        String query = buildQuery(MessageFormat.format("SELECT * WHERE '{'" +
                "<{0}> rdfs:label ?{1}.\n" +
                "<{0}> foaf:isPrimaryTopicOf ?{2}.\n" +
                "OPTIONAL '{' <{0}> dbo:thumbnail ?{3}. '}'\n" +
                "OPTIONAL '{' <{0}> dbo:abstract ?{4}. FILTER(lang(?{4}) = \"en\"). '}'\n" +
                "FILTER(lang(?{1}) = \"en\")" +
            "'}'", uri, VAR_LABEL, VAR_PRIMARY_TOPIC, VAR_THUMBNAIL, VAR_ABSTRACT));
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

    private ArrayList<ResponseData> getEntities(String query) {
        QueryExecution queryExecution = executeQuery(query);
        ArrayList<ResponseData> responseDatas = null;

        try {
            Iterator<QuerySolution> results = queryExecution.execSelect();
            if (results != null) {
                responseDatas = new ArrayList<>();
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

    public ArrayList<ResponseData> getDisambiguatedEntities(String uri, int offset, int limit) {
        String query = buildQuery(MessageFormat.format("SELECT * WHERE '{'\n" +
                "<{0}> <http://dbpedia.org/ontology/wikiPageDisambiguates> ?{1} .\n" +
                "?{1} rdfs:label ?{2} .\n" +
                "?{1} foaf:isPrimaryTopicOf ?{3} .\n" +
                "OPTIONAL '{' ?{1} dbo:thumbnail ?{4}. '}' .\n" +
                "OPTIONAL '{' ?{1} dbo:abstract ?{5}. FILTER(lang(?{5}) = \"en\"). '}'\n" +
                "FILTER(lang(?{2}) = \"en\") .\n" +
        "'}' ORDER BY ?{1} offset {6} limit {7}", uri, VAR_URI, VAR_LABEL, VAR_PRIMARY_TOPIC, VAR_THUMBNAIL, VAR_ABSTRACT, offset, limit));
        return getEntities(query);
    }

    public ArrayList<ResponseData> getEntitiesByURIs(String uris) {
        String query = buildQuery(MessageFormat.format("SELECT * WHERE '{'\n" +
                "VALUES ?{1} '{' {0} '}' .\n" +
                "?{1} rdfs:label ?{2} .\n" +
                "?{1} foaf:isPrimaryTopicOf ?{3} .\n" +
                "OPTIONAL '{' ?{1} dbo:thumbnail ?{4}. '}' .\n" +
                "OPTIONAL '{' ?{1} dbo:abstract ?{5}. FILTER(lang(?{5}) = \"en\"). '}'\n" +
                "FILTER(lang(?{2}) = \"en\") .\n" +
                "'}'", uris, VAR_URI, VAR_LABEL, VAR_PRIMARY_TOPIC, VAR_THUMBNAIL, VAR_ABSTRACT));
        return getEntities(query);
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

    }

    public static class ResponseInfo {
        public static final String DISAMBIGUATION_PAGE = "Disambiguation Page";
//        public static final String DISAMBIGUATION_PAGE = "Disambiguation Page";

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
