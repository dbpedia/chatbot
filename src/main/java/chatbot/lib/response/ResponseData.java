package chatbot.lib.response;

/**
 * Created by ramgathreya on 5/22/17.
 */
public class ResponseData {
    private String text;

    public String getText() {
        return text;
    }

    public ResponseData setText(String text) {
        this.text = text;
        return this;
    }

    public ResponseData(String text) {
        this.text = text;
    }
}
