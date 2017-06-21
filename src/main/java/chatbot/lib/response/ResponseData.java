package chatbot.lib.response;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ramgathreya on 5/22/17.
 */
public class ResponseData {
    private String image;
    private String title;
    private String text;
    private List<Button> buttons = new ArrayList<>();
    private List<SmartReply> smartReplies = new ArrayList<>();

    public static final int MAX_DATA_SIZE = 5;
    public static final int MAX_BUTTON_SIZE = 3;
    public static final int MAX_SMART_REPLY_SIZE = 10;

    public ResponseData addButton(Button button) {
        this.buttons.add(button);
        return this;
    }

    public ResponseData addSmartReply(SmartReply smartReply) {
        this.smartReplies.add(smartReply);
        return this;
    }

    public List<SmartReply> getSmartReplies() {
        return smartReplies;
    }

    public ResponseData setSmartReplies(List<SmartReply> smartReplies) {
        this.smartReplies = smartReplies;
        return this;
    }

    public List<Button> getButtons() {
        return buttons;
    }

    public ResponseData setButtons(List<Button> buttons) {
        this.buttons = buttons;
        return this;
    }

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

    // Constructor for Button Text Data
    public ResponseData(String text, ArrayList<Button> button) {
        this.text = text;
        this.buttons = button;
    }

    // Constructor for Carousel Data
    public ResponseData(String image, String title, String text) {
        this.image = image;
        this.title = title;
        this.text = text;
    }

    public ResponseData() {

    }

    public static class Button {
        private String title;
        private String buttonType;
        private String uri;

        public Button(String title, String buttonType, String uri) {
            this.title = title;
            this.buttonType = buttonType;
            this.uri = uri;
        }

        public String getTitle() {
            return title;
        }

        public Button setTitle(String title) {
            this.title = title;
            return this;
        }

        public String getButtonType() {
            return buttonType;
        }

        public Button setButtonType(String buttonType) {
            this.buttonType = buttonType;
            return this;
        }

        public String getUri() {
            return uri;
        }

        public Button setUri(String uri) {
            this.uri = uri;
            return this;
        }
    }

    public static class SmartReply {
        private String title;
        private String uri;

        public SmartReply(String title, String uri) {
            this.title = title;
            this.uri = uri;
        }

        public String getTitle() {
            return title;
        }

        public SmartReply setTitle(String title) {
            this.title = title;
            return this;
        }

        public String getUri() {
            return uri;
        }

        public SmartReply setUri(String uri) {
            this.uri = uri;
            return this;
        }
    }
}
