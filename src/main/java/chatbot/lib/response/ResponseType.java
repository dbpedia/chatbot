package chatbot.lib.response;

/**
 * Created by ramgathreya on 5/22/17.
 */
public interface ResponseType {
    String TEXT_MESSAGE = "text";
    String GENERIC_MESSAGE = "generic";
    String BUTTON_TEXT_MESSAGE = "button_text";
    String SMART_REPLY_MESSAGE = "smart_reply";

    String BUTTON_LINK = "link";
    String BUTTON_PARAM = "param";
}
