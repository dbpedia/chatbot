package chatbot.lib.request;

/**
 * Created by ramgathreya on 5/23/17.
 */
public class MessageData {
    private String text;
    private String payload;

    public String getPayload() {
        return payload;
    }

    public MessageData setPayload(String payload) {
        this.payload = payload;
        return this;
    }

    public String getText() {
        return text;
    }

    public MessageData setText(String text) {
        this.text = text;
        return this;
    }
}