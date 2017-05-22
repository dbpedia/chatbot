package chatbot.app.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ramgathreya on 5/19/17.
 */
public class Request {
    private static final Logger logger = LoggerFactory.getLogger(Request.class);

    private String messageType;
    private List<MessageData> messageData = new ArrayList<>();

    public List<MessageData> getMessageData() {
        return messageData;
    }

    public Request setMessageData(List<MessageData> messageData) {
        this.messageData = messageData;
        return this;
    }

    public Request setMessageType(String messageType) {
        this.messageType = messageType;
        return this;
    }

    public String getMessageType() {
        return this.messageType;

    }

    public String getMessage() {
        logger.info("MESSAGE DATA SIZE: " + messageData.size());
        return messageData.get(0).getText();
    }

    static class MessageData {
        public String text;

        public String getText() {
            return text;
        }

        public MessageData setText(String text) {
            this.text = text;
            return this;
        }
    }
}
