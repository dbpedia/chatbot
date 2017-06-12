package chatbot.rivescript;

/**
 * Created by ramgathreya on 6/4/17.
 */
public interface RiveScriptReplyType {
    String FALLBACK = "fallback";
    String TEMPLATE = "template";
    String STATUS_CHECK = "status_check";

    // String Scenarios where we just want different kinds of messages from Rive
    String FALLBACK_TEXT = "fallbacktext";
    String HELP_TEXT = "helptext";
    String START_TEXT = "starttext";
    String NL_ANSWER_TEXT = "nlanswertext";
    String STATUS_CHECK_TEXT = "statuschecktext";

    String YES_TEXT = "yes";
    String NO_TEXT = "no";
}
