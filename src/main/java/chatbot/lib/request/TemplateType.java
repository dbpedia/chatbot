package chatbot.lib.request;

/**
 * Created by ramgathreya on 5/23/17.
 */
public interface TemplateType {
    String START = "start";
    String HELP = "help";
    String CHECK_SERVICE = "check_service";

    String DBPEDIA_ABOUT = "dbpedia-about";
    String DBPEDIA_CONTRIBUTE = "dbpedia-contribute";
    String DBPEDIA_FALLBACK = "dbpedia-fallback";
    String DBPEDIA_ASSOCIATION = "dbpedia-association";

    String DBPEDIA_DATASET = "dbpedia-dataset";
    String DBPEDIA_DATASET_NLP = "dbpedia-dataset-nlp";
    String DBPEDIA_DATASET_ONTOLOGY = "dbpedia-dataset-ontology";
    String DBPEDIA_DATABUS_RECOMMENDATION = "dbpedia-databus-recommendation";

    String DBPEDIA_LOOKUP = "dbpedia-lookup";
    String DBPEDIA_LOOKUP_PREFIX_SEARCH = "dbpedia-lookup-prefix-search";
    String DBPEDIA_LOOKUP_KEYWORD_SEARCH = "dbpedia-lookup-keyword-search";
    String DBPEDIA_LOOKUP_PARAMETERS = "dbpedia-lookup-parameters";

    String DBPEDIA_MAPPINGS = "dbpedia-mappings";
    String DBPEDIA_MAPPINGS_TOOL = "dbpedia-mappings-tool";
    String DBPEDIA_MAPPINGS_LOGIN = "dbpedia-mappings-login";

    String DBPEDIA_EXTRACTION_FRAMEWORK = "dbpedia-extraction-framework";

    String DBPEDIA_GSOC = "dbpedia-gsoc";

    String GET_LOCATION = "get-location";

    String TV_CAST = "tv-cast";
    String TV_CREW = "tv-crew";
    String TV_SIMILAR = "tv-similar";
    String TV_RELATED = "tv-related";

    String MOVIE_CAST = "movie-cast";
    String MOVIE_CREW = "movie-crew";
    String MOVIE_SIMILAR = "movie-similar";
    String MOVIE_RELATED = "movie-related";

    String ENTITY_INFORMATION = "entity-information";

    String LOAD_MORE = "load-more";
    String LOAD_RELATED = "load-related";
    String LOAD_SIMILAR = "load-similar";

    String LEARN_MORE = "learn-more";
    String FEEDBACK = "feedback";
    String FAQ = "faq";

    String YES = "yes";
    String NO = "no";
}
