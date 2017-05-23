package chatbot.lib.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ramgathreya on 5/19/17.
 */
public class Request {
    private static final Logger logger = LoggerFactory.getLogger(Request.class);
    private String userId;
    private String messageType;
    private List<MessageData> messageData = new ArrayList<>();
    private String platform;

    public Request() {

    }

    // Constructor for FB Messenger
    public Request(String userId, String messageType, String platform) {
        this.userId = userId;
        this.messageType = messageType;
        this.platform = platform;
    }

    public String getPlatform() {
        return platform;
    }

    public Request setPlatform(String platform) {
        this.platform = platform;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public Request setUserId(String userId) {
        this.userId = userId;
        return this;
    }

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

    // Written for convenience
    public String getText() {
        return this.getMessageData().get(0).getText();
    }

    // Written for convenience
    public String getPayload() {
        return this.getMessageData().get(0).getPayload();
    }
}
