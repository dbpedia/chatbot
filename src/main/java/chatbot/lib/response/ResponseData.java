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
    private List<ButtonData> buttons = new ArrayList<>();
    public static final int MAX_DATA_SIZE = 5;
    public static final int MAX_BUTTON_SIZE = 3;

    public ResponseData addButton(ButtonData buttonData) {
        this.buttons.add(buttonData);
        return this;
    }

    public List<ButtonData> getButtons() {
        return buttons;
    }

    public ResponseData setButtons(List<ButtonData> buttons) {
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
    public ResponseData(String text, ArrayList<ButtonData> buttonData) {
        this.text = text;
        this.buttons = buttonData;
    }

    // Constructor for Carousel Data
    public ResponseData(String image, String title, String text) {
        this.image = image;
        this.title = title;
        this.text = text;
    }

    public ResponseData() {

    }

    public static class ButtonData {
        private String title;
        private String buttonType;
        private String uri;

        public ButtonData(String title, String buttonType, String uri) {
            this.title = title;
            this.buttonType = buttonType;
            this.uri = uri;
        }

        public String getTitle() {
            return title;
        }

        public ButtonData setTitle(String title) {
            this.title = title;
            return this;
        }

        public String getButtonType() {
            return buttonType;
        }

        public ButtonData setButtonType(String buttonType) {
            this.buttonType = buttonType;
            return this;
        }

        public String getUri() {
            return uri;
        }

        public ButtonData setUri(String uri) {
            this.uri = uri;
            return this;
        }
    }
}
