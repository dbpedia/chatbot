package chatbot.couchdb;

import java.util.Date;

/**
 * Created by ramgathreya on 6/30/17.
 */
public class FeedbackModel {
    private static final int MIN_TITLE_LENGTH = 5;
    private static final int MIN_DESCRIPTION_LENGTH = 20;

    private String title;
    private String description;
    private String userId;
    private long timestamp;

    public FeedbackModel() {
        timestamp = new Date().getTime();
    }

    public String getTitle() {
        return title;
    }

    public FeedbackModel setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public FeedbackModel setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public FeedbackModel setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public boolean validate() {
        if(title.length() >= MIN_TITLE_LENGTH && description.length() >= MIN_DESCRIPTION_LENGTH) {
            return true;
        }
        return false;
    }
}
