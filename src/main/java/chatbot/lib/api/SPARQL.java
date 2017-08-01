package chatbot.lib.api;

import chatbot.lib.Constants;
import chatbot.lib.Utility;
import chatbot.lib.request.TemplateType;
import chatbot.lib.response.ResponseData;
import chatbot.lib.response.ResponseType;
import com.cloudant.client.api.Database;
import com.cloudant.client.api.views.Key;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by ramgathreya on 6/2/17.
 */

// https://stackoverflow.com/questions/1644252/querying-dbpedia-with-sparql-and-jena
// https://jena.apache.org/documentation/query/app_api.html
// http://tutorial-academy.com/apache-jena-tdb-crud-operations/

public class SPARQL {
    private static final Logger logger = LoggerFactory.getLogger(SPARQL.class);

    private static final String ENDPOINT = "https://dbpedia.org/sparql";
    private static final String PREFIXES = new String(
            "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" +
            "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
            "PREFIX dbo: <http://dbpedia.org/ontology/>\n" +
            "PREFIX dbp: <http://dbpedia.org/property/>\n" +
            "PREFIX dbr: <http://dbpedia.org/resource/>\n" +
            "PREFIX dct: <http://purl.org/dc/terms/>\n"
    );

    Database explorerDB;
    public SPARQL(Database explorerDB) {
        this.explorerDB = explorerDB;
    }

    private static final String ENTITY_SELECT_PARAMETERS = " ?label ?abstract ?primaryTopic ?thumbnail ?description ";

    public String buildQuery(String query) {
        return PREFIXES + query;
    }

    // Remove the pronounciation information that appears at the beginning of the article enclosed by ()
    // Additionally can be checked to contain unnecessary characters instead of blindly stripping based on brackets
    private String stripWikiepdiaContent(String text) {
        int indexStart = text.indexOf("("), indexEnd;
        if(indexStart > 0) {
            indexEnd = text.indexOf(")", indexStart) + 2;
            if(indexEnd != -1) {
                return text.replace(text.substring(indexStart, indexEnd), "");
            }
        }
        else if(indexStart == 0) {
            // When abstract starts with info on Disambiguation
            indexEnd = text.lastIndexOf("(disambiguation).)");
            if(indexEnd != -1) {
                return text.replace(text.substring(indexStart, indexEnd + 18), "");
            }
        }
        return text;
    }

    private String processWikipediaAbstract(String abs) {
        while(abs.indexOf("(") != -1) {
            abs = stripWikiepdiaContent(abs);
        }
        return abs;
    }

    private class ExplorerProperties {
        private String className;
        private String property;
        private String score;

        public String getClassName() {
            return className;
        }

        public ExplorerProperties setClassName(String className) {
            this.className = className;
            return this;
        }

        public String getProperty() {
            return property;
        }

        public ExplorerProperties setProperty(String property) {
            this.property = property;
            return this;
        }

        public String getScore() {
            return score;
        }

        public ExplorerProperties setScore(String score) {
            this.score = score;
            return this;
        }
    }

    private List<ResponseData.Field> getRelevantProperties(String uri, List types, String[] properties) {
        List<ResponseData.Field> fields = new ArrayList<>();
        try {
            TreeMap<Float, String> propertyMap = new TreeMap<>();
            List<ExplorerProperties> explorerProperties = explorerDB.getViewRequestBuilder("explorer", "getProperties")
                    .newRequest(Key.Type.STRING, ExplorerProperties.class)
                    .keys(properties)
                    .inclusiveEnd(true)
                    .build().getResponse().getValues();


            for(ExplorerProperties property : explorerProperties) {
                // Check if the property matches one of the list of classes(types) found for the entity
                if(types.contains(property.getClassName())) {
                    propertyMap.put(Float.parseFloat(property.getScore()), property.getProperty());
                }
            }

            if(propertyMap.size() > 0) {
                int count = 0;
                Iterator<Float> iterator = propertyMap.descendingKeySet().iterator();
                String property_uris = "";
                while (count < ResponseData.MAX_FIELD_SIZE && iterator.hasNext()) {
                    property_uris += "<" + propertyMap.get(iterator.next()) + "> ";
                    count++;
                }

                String query = buildQuery("SELECT ?property_label (group_concat(distinct ?value;separator='__') as ?values) (group_concat(distinct ?value_label;separator='__') as ?value_labels) where {\n" +
                        "VALUES ?property {" + property_uris + "}\n" +
                        "<" + uri + "> ?property ?value . \n" +
                        "?property rdfs:label ?property_label . FILTER(lang(?property_label)='en'). \n" +
                        "OPTIONAL {?value rdfs:label ?value_label . FILTER(lang(?value_label) = 'en') }\n" +
                        "} GROUP BY ?property_label");
                QueryExecution queryExecution = executeQuery(query);
                try {
                    Iterator<QuerySolution> results = queryExecution.execSelect();
                    while(results.hasNext()) {
                        QuerySolution result = results.next();
                        ResponseData.Field field = new ResponseData.Field();
                        field.setName(Utility.capitalizeAll(result.get("property_label").asLiteral().getString()));

                        // If Value Label String is empty then we use Value String instead which means the value is a literal. So we are only taking the first element before space
                        if(result.get("value_labels").asLiteral().getString().equals("")) {
                            field.setValue(Utility.capitalizeAll(result.get("values").asLiteral().getString().split("__")[0]));
                        }
                        else {
                            LinkedHashMap<String, String> map = new LinkedHashMap<>();
                            String[] keyArray = result.get("values").asLiteral().getString().split("__");
                            String[] valueArray = result.get("value_labels").asLiteral().getString().split("__");

                            for(int index = 0; index < keyArray.length; index++) {
                                map.put(Utility.convertDBpediaToWikipediaURL(keyArray[index]), valueArray[index]);
                            }
                            field.setValues(map);
                        }
                        fields.add(field);
                    }
                    return fields;
                }
                finally {
                    queryExecution.close();
                    return fields;
                }
            }
            else {
                return fields;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return fields;
    }

    private ResponseData processEntityInformation(String uri, QuerySolution result) {
        RDFNode node;
        ResponseData responseData = new ResponseData();
        String label = result.get("label").asLiteral().getString(), text = "";
        responseData.setTitle(label);
        responseData.addButton(new ResponseData.Button("View in Wikipedia", ResponseType.BUTTON_LINK, result.get("primaryTopic").toString()));

        node = result.get("thumbnail");
        if(node != null) {
            responseData.setImage(node.toString());
        }

//        String summary = new GenesisService().getSummary(uri);
//        if(summary == null || summary.isEmpty()) {
//            node = result.get(VAR_ABSTRACT);
//            if(node != null) {
//                summary = processWikipediaAbstract(node.asLiteral().getString());
//            }
//        }

        node = result.get("description");
        if(node != null) {
            text += node.asLiteral().getString() + "\n\n";
        }

        node = result.get("abstract");
        if (node != null) {
            String summary = processWikipediaAbstract(node.asLiteral().getString());
            text += summary;
        }

        if (!text.equals("")) {
            responseData.setText(text);
        }

        String query = buildQuery(
                "SELECT (GROUP_CONCAT(distinct ?type;separator=' ') as ?types) (GROUP_CONCAT(distinct ?property;separator=' ') as ?properties) WHERE {\n" +
                "<" + uri + "> rdf:type ?type . FILTER(STRSTARTS(STR(?type), 'http://dbpedia.org/ontology')) . \n" +
                "<" + uri + "> ?property ?value . FILTER(STRSTARTS(STR(?property), 'http://dbpedia.org/ontology')) . \n" +
        "}");
        QueryExecution queryExecution = executeQuery(query);
        try {
            Iterator<QuerySolution> results = queryExecution.execSelect();
            while(results.hasNext()) {
                QuerySolution solution = results.next();
                if(solution.get("types") != null && solution.get("properties") != null) {
                    List<String> types = Arrays.asList(solution.get("types").asLiteral().getString().split(" "));
                    String[] properties = solution.get("properties").asLiteral().getString().split(" ");
                    responseData.setFields(getRelevantProperties(uri, types, properties));
                }
            }
        }
        finally {
            queryExecution.close();
        }

        responseData.addButton(new ResponseData.Button("View in DBpedia", ResponseType.BUTTON_LINK, uri));
        responseData.addButton(new ResponseData.Button("Learn More", ResponseType.BUTTON_PARAM, TemplateType.LEARN_MORE + Utility.STRING_SEPARATOR + uri + Utility.STRING_SEPARATOR + label));
        return responseData;
    }

    private String getEntityWhereCondition(String uri) {
        // URI could either be the actual URI or a variable reference
        // In case of actual URI it needs to be enclosed with <uri> which is not required for variable reference
        if(!uri.startsWith("?")) {
            uri = "<" + uri + ">";
        }
        return uri + " rdfs:label ?label .\n" +
                uri + " foaf:isPrimaryTopicOf ?primaryTopic .\n" +
                "OPTIONAL {" + uri + " dbo:thumbnail ?thumbnail . }\n" +
                "OPTIONAL {" + uri + " dbo:abstract ?abstract . FILTER(lang(?abstract)=\"en\") }\n" +
                "OPTIONAL {" + uri + " dct:description ?description . FILTER(lang(?description)=\"en\") }\n" +
                "FILTER(lang(?label) = 'en')\n";
    }

    public ResponseData getEntityInformation(String uri) {
        String query = buildQuery("SELECT " + ENTITY_SELECT_PARAMETERS + " WHERE {" +
                getEntityWhereCondition(uri) +
        "}");
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
            while(results.hasNext()) {
                QuerySolution result = results.next();
                count = result.get("count").asLiteral().getInt();
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
        String query = buildQuery("SELECT ?uri " + ENTITY_SELECT_PARAMETERS + " WHERE {\n" +
                "<" + uri + "> <http://dbpedia.org/ontology/wikiPageDisambiguates> ?uri .\n" +
                getEntityWhereCondition("?uri") +
        "} ORDER BY ?uri OFFSET " + offset + " LIMIT " + limit);
        return getEntities(query);
    }

    public ArrayList<ResponseData> getEntitiesByURIs(String uris) {
        String query = buildQuery("SELECT ?uri " + ENTITY_SELECT_PARAMETERS + " WHERE {\n" +
                "VALUES ?uri { " + uris + "} .\n" +
                getEntityWhereCondition("?uri") +
                "}");
        return getEntities(query);
    }

    public String getLabel(String uri) {
        String label = null;
        String query = buildQuery("SELECT * WHERE {\n" +
                "<" + uri + "> rdfs:label ?label .\n" +
                "FILTER(lang(?label) = 'en') ." +
                "}");
        QueryExecution queryExecution = executeQuery(query);

        try {
            Iterator<QuerySolution> results = queryExecution.execSelect();
            label = results.next().get("label").asLiteral().getString();
        }
        finally {
            queryExecution.close();
        }
        return label;
    }

    public String getRDFTypes(String uri) {
        String types = null;
        String query = buildQuery("SELECT (group_concat(?type;separator=' ') as ?types) WHERE {" +
                "<" + uri + "> rdf:type ?type ." +
        "}");
        QueryExecution queryExecution = executeQuery(query);

        try {
            Iterator<QuerySolution> results = queryExecution.execSelect();
            if (results != null) {
                while(results.hasNext()) {
                    QuerySolution result = results.next();
                    types = result.get("types").toString();
                }
            }
        }
        finally {
            queryExecution.close();
        }
        return types;
    }

    public QueryExecution executeQuery(String queryString) {
        logger.info("SPARQL Query is:\n" + queryString);
        Query query = QueryFactory.create(queryString);
        QueryEngineHTTP queryEngine = (QueryEngineHTTP) QueryExecutionFactory.sparqlService(ENDPOINT, query);
        queryEngine.addParam("timeout", String.valueOf(Constants.API_TIMEOUT));
        return queryEngine;
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

        public ArrayList<ResponseData> nextPage(SPARQL sparql) {
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
