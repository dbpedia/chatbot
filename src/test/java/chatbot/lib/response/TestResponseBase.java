package chatbot.lib.response;


import chatbot.lib.TestUtility;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by ramgathreya on 6/20/17.
 */
public class TestResponseBase {
    public static void checkTextMessage(Response response) {
        List<ResponseData> messageData = response.getMessageData();
        assertEquals(response.getMessageType(), ResponseType.TEXT_MESSAGE);
        assertEquals(messageData.size(), 1);
        assertNotNull(messageData.get(0).getText());
    }

    public static void checkButtons(List<ResponseData.Button> buttons) {
        assertTrue("Button Data Size Greater Than 0", buttons.size() > 0);
        assertTrue("Button Data Size Less Than Equal to " + ResponseData.MAX_BUTTON_SIZE, buttons.size() <= ResponseData.MAX_BUTTON_SIZE);
        for (ResponseData.Button button : buttons) {
            assertNotNull(button.getTitle());
            switch(button.getButtonType()) {
                case ResponseType.BUTTON_LINK:
                    TestUtility.checkUrl(button.getUri());
                    break;
                case ResponseType.BUTTON_PARAM:
                    break;
                default:
                    // Should not come here
                    fail("Button has invalid type: " + button.getButtonType());
            }
        }
    }

    public static void checkCarouselMessage(Response response) {
        List<ResponseData> messageData = response.getMessageData();
        assertEquals(response.getMessageType(), ResponseType.CAROUSEL_MESSAGE);
        assertTrue("Message Data Size Greater Than 0", messageData.size() > 0);
        assertTrue("Message Data Size Less Than Equal to " + ResponseData.MAX_DATA_SIZE, messageData.size() <= ResponseData.MAX_DATA_SIZE);

        for(ResponseData responseData : messageData) {
            assertNotNull(responseData.getTitle());
            String image = responseData.getImage();
            List<ResponseData.Button> buttons = responseData.getButtons();
            if (image != null) {
                TestUtility.checkUrl(image);
            }
            if (buttons.size() > 0) {
                checkButtons(buttons);
            }
        }
    }
}
