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

    String TV_CAST = "tv-cast";
    String TV_CREW = "tv-crew";
    String TV_SIMILAR = "tv-similar";
    String TV_RELATED = "tv-related";

    String MOVIE_CAST = "movie-cast";
    String MOVIE_CREW = "movie-crew";
    String MOVIE_SIMILAR = "movie-similar";
    String MOVIE_RELATED = "movie-related";

    String LOAD_MORE = "load-more";
    String LOAD_RELATED = "load-related";
    String LOAD_SIMILAR = "load-similar";

    String LEARN_MORE = "learn-more";
    String FEEDBACK = "feedback";
    String FAQ = "faq";

    String YES = "yes";
    String NO = "no";
}
