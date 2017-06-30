package chatbot.couchdb;

import chatbot.lib.request.Request;
import chatbot.lib.response.Response;

import java.util.Date;
import java.util.List;

/**
 * Created by ramgathreya on 6/23/17.
 */
//@Document(expiry=0)
public class Chat {
    public static final String ID_SEPARATOR = "_";

    private String id;
    private String userId;
    private boolean fromBot = false;
    private Request request;
    private List<Response> response;
    private long timestamp;

    public String getUserId() {
        return userId;
    }

    public Chat setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public boolean isFromBot() {
        return fromBot;
    }

    public Chat setFromBot(boolean fromBot) {
        this.fromBot = fromBot;
        return this;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Chat setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public Request getRequest() {
        return request;
    }

    public Chat setRequest(Request request) {
        this.request = request;
        return this;
    }

    public List<Response> getResponse() {
        return response;
    }

    public Chat setResponse(List<Response> response) {
        this.response = response;
        return this;
    }

    public String getId() {
        return id;
    }

    public Chat setId(String id) {
        this.id = id;
        return this;
    }
}