package chatbot.lib.response;

/**
 * Created by ramgathreya on 5/22/17.
 */
public class ResponseData {
    private String image;
    private String title;
    private String text;

    public String getImage() {
        return image;
    }

    public ResponseData setImage(String image) {
        this.image = image;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public ResponseData setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getText() {
        return text;
    }

    public ResponseData setText(String text) {
        this.text = text;
        return this;
    }

    // Constructor for Text Data
    public ResponseData(String text) {
        this.text = text;
    }

    // Constructor for Carousel Data
    public ResponseData(String image, String title, String text) {
        this.image = image;
        this.title = title;
        this.text = text;
    }
}
