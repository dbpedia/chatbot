package chatbot.lib.response;


import chatbot.lib.TestUtility;
import org.junit.Ignore;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by ramgathreya on 6/20/17.
 */
@Ignore
public class TestResponseBase {
    public static void checkTextMessage(Response response) {
        List<ResponseData> messageData = response.getMessageData();
        assertEquals(response.getMessageType(), ResponseType.TEXT_MESSAGE);
        assertEquals(messageData.size(), 1);
        assertNotNull(messageData.get(0).getText());
        assertEquals(messageData.get(0).getText().contains("\n"), false); // Check that we are showing only 1 response and not both responses from both QA Systems
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
        assertEquals(response.getMessageType(), ResponseType.GENERIC_MESSAGE);
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
