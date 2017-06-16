package chatbot.rivescript;

/**
 * Created by ramgathreya on 6/4/17.
 */
public interface RiveScriptReplyType {
    String FALLBACK_SCENARIO = "fallback";
    String TEMPLATE_SCENARIO = "template";
    String STATUS_CHECK_SCENARIO = "status_check";
    String LANGUAGE_SCENARIO = "language";

    // String Scenarios where we just want different kinds of messages from Rive
    String FALLBACK_TEXT = "fallbacktext";
    String HELP_TEXT = "helptext";
    String START_TEXT = "starttext";
    String NL_ANSWER_TEXT = "nlanswertext";
    String STATUS_CHECK_TEXT = "statuschecktext";

    String DBPEDIA_CONTRIBUTE_TEXT = "dbpediacontributetext";
    String DISAMBIGUATION_TEXT = "disambiguationtext";
    String LOAD_MORE_TEXT = "loadmoretext";

    String YES_TEXT = "yes";
    String NO_TEXT = "no";
}
