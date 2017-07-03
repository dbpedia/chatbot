package chatbot.lib;

/**
 * Created by ramgathreya on 6/3/17.
 */
public interface Ontology {
    String RDFS_LABEL = "http://www.w3.org/2000/01/rdf-schema#label";

    String DBO_THUMBNAIL = "http://dbpedia.org/ontology/thumbnail";
    String DBO_ABSTRACT = "http://dbpedia.org/ontology/abstract";
    String DBO_TELEVISION_SHOW = "http://dbpedia.org/ontology/TelevisionShow";
    String DBO_FILM = "http://dbpedia.org/ontology/Film";

    String FOAF_IS_PRIMARY_TOPIC_OF = "http://xmlns.com/foaf/0.1/isPrimaryTopicOf";
}
