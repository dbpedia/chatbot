package chatbot.couchbase;

import chatbot.lib.request.Request;
import chatbot.lib.response.Response;
import com.couchbase.client.java.repository.annotation.Field;
import com.couchbase.client.java.repository.annotation.Id;
import org.springframework.data.couchbase.core.mapping.Document;

import java.util.Date;
import java.util.List;

/**
 * Created by ramgathreya on 6/23/17.
 */
@Document(expiry=0)
public class Chat {
    public static final String ID_SEPARATOR = "_";

    @Id
    private String id;

    @Field
    private String userId;

    @Field
    private boolean fromBot = false;

    @Field
    private Request request;

    @Field
    private List<Response> response;

    @Field
    private Date date;

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

    public Date getDate() {
        return date;
    }

    public Chat setDate(Date date) {
        this.date = date;
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