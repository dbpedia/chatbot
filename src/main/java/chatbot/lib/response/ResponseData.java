package chatbot.lib.response;

import chatbot.lib.Colors;
import chatbot.platforms.Platform;

import java.util.ArrayList;
import java.util.LinkedHashMap;
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
    private List<Field> fields = new ArrayList<>();

    public static final int MAX_DATA_SIZE = 5;
    public static final int MAX_BUTTON_SIZE = 3;
    public static final int MAX_SMART_REPLY_SIZE = 10;
    public static final int MAX_FIELD_SIZE = 5;

    public List<Field> getFields() {
        return fields;
    }

    public ResponseData setFields(List<Field> fields) {
        this.fields = fields;
        return this;
    }

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
        private String slackStyle = Platform.SLACK_BUTTON_DEFAULT;

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

        public String getSlackStyle() {
            return slackStyle;
        }

        public Button setSlackStyle(String slackStyle) {
            this.slackStyle = slackStyle;
            return this;
        }
    }

    public static class SmartReply {
        private String title;
        private String uri;
        private String slackStyle = Platform.SLACK_BUTTON_DEFAULT;

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

        public String getSlackStyle() {
            return slackStyle;
        }

        public SmartReply setSlackStyle(String slackStyle) {
            this.slackStyle = slackStyle;
            return this;
        }
    }

    public static class Field {
        private String name;
        private String value;
        private LinkedHashMap<String, String> values = null;
        private boolean isShort = true;

        public Field() {

        }

        public String getName() {
            return name;
        }

        public Field setName(String name) {
            this.name = name;
            return this;
        }

        public String getValue() {
            return value;
        }

        public Field setValue(String value) {
            this.value = value;
            return this;
        }

        public Field(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public Field(String name, String value, boolean isShort) {
            this.name = name;
            this.value = value;
            this.isShort = isShort;
        }

        public boolean isShort() {
            return isShort;
        }

        public Field setShort(boolean aShort) {
            isShort = aShort;
            return this;
        }

        public LinkedHashMap<String, String> getValues() {
            return values;
        }

        public Field setValues(LinkedHashMap<String, String> values) {
            this.values = values;
            return this;
        }

        public String getFieldValue(String platform) {
            switch (platform) {
                case Platform.SLACK:
                    String result = "";
                    if (values != null) {
                        for(String key : values.keySet()) {
                            result += "<" + key + "|" + values.get(key) + ">, ";
                        }
                        return result.substring(0, result.length() - 2);
                    }
                    break;
            }
            return value;
        }
    }
}
