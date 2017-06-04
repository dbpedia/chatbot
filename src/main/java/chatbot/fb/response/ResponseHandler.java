package chatbot.fb.response;

import chatbot.lib.Utility;
import chatbot.lib.request.Request;
import chatbot.lib.response.Response;
import chatbot.lib.response.ResponseData;
import chatbot.lib.response.ResponseType;
import com.github.messenger4j.exceptions.MessengerApiException;
import com.github.messenger4j.exceptions.MessengerIOException;
import com.github.messenger4j.send.MessengerSendClient;
import com.github.messenger4j.send.NotificationType;
import com.github.messenger4j.send.Recipient;
import com.github.messenger4j.send.buttons.Button;
import com.github.messenger4j.send.templates.GenericTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ramgathreya on 5/23/17.
 */
public class ResponseHandler {
    private static final int  MAX_SUBTITLE_LENGTH = 80;

    private static final Logger logger = LoggerFactory.getLogger(ResponseHandler.class);

    private Request request;
    private MessengerSendClient sendClient;
    private List<Response> responseList;
    private String baseUrl;

    public ResponseHandler(Request request, MessengerSendClient sendClient, List<Response> responseList, String baseUrl) {
        this.request = request;
        this.sendClient = sendClient;
        this.responseList = responseList;
        this.baseUrl = baseUrl;
    }

    public void generateResponse() throws MessengerApiException, MessengerIOException {
        for(Response response : responseList) {
            switch(response.getMessageType()) {
                case ResponseType.TEXT:
                    sendTextMessage(request.getUserId(), response.getMessageData().get(0).getText());
                    break;
                case ResponseType.CAROUSEL:
                    sendGenericMessage(request.getUserId(), response.getMessageData());
                    break;
            }
        }
    }

    private void sendTextMessage(String recipientId, String text) {
        try {
            final Recipient recipient = Recipient.newBuilder().recipientId(recipientId).build();
            final NotificationType notificationType = NotificationType.REGULAR;
            sendClient.sendTextMessage(recipient, notificationType, text);
        } catch (MessengerApiException | MessengerIOException e) {
            handleSendException(e);
        }
    }

    private String processSubtitle(String subtitle) {
        if(subtitle.length() <= MAX_SUBTITLE_LENGTH) {
            return subtitle;
        }
        return subtitle.substring(0, MAX_SUBTITLE_LENGTH - 3) + "...";
    }

    private void sendGenericMessage(String recipientId, List<ResponseData> items) throws MessengerApiException, MessengerIOException {
//        final List<Button> riftButtons = Button.newListBuilder()
//                .addUrlButton("Open Web URL", "https://www.oculus.com/en-us/rift/").toList()
//                .addPostbackButton("Call Postback", "Payload for first bubble").toList()
//                .build();
//

        GenericTemplate.Element.ListBuilder genericTemplate = GenericTemplate.newBuilder().addElements();

        for(ResponseData item : items) {
            GenericTemplate.Element.Builder element = genericTemplate.addElement(item.getTitle());
            element.imageUrl(Utility.generateImageUrl(baseUrl, item.getImage()))
                .subtitle(processSubtitle(item.getText()));

            List<ResponseData.ButtonData> buttons = item.getButtons();
            if(buttons != null && buttons.size() > 0) {
                Button.ListBuilder fbButtons = Button.newListBuilder();
                for(ResponseData.ButtonData button : buttons) {
                    switch(button.getButtonType()) {
                        case ResponseType.BUTTON_LINK:
                            fbButtons.addUrlButton(button.getTitle(), button.getUri()).toList();
                            break;
                        case ResponseType.BUTTON_PARAM:
                            fbButtons.addPostbackButton(button.getTitle(), button.getUri()).toList();
                            break;
                    }
                }
                element.buttons(fbButtons.build());
            }

            element.toList();
        }
        sendClient.sendTemplate(recipientId, genericTemplate.done().build());


//        final GenericTemplate genericTemplatez = GenericTemplate.newBuilder()
//                .addElements()
//                .addElement("rift")
//                .subtitle("Next-generation virtual reality")
//                .itemUrl("https://www.oculus.com/en-us/rift/")
//                .imageUrl("/assets/rift.png")
////                .buttons(riftButtons)
//                .toList()
//                .addElement("touch")
//                .subtitle("Your Hands, Now in VR")
//                .itemUrl("https://www.oculus.com/en-us/touch/")
//                .imageUrl("/assets/touch.png")
////                .buttons(touchButtons)
//                .toList()
//                .done()
//                .build();

//        this.sendClient.sendTemplate(recipientId, genericTemplate);
    }

    private void handleSendException(Exception e) {
        logger.error("Message could not be sent. An unexpected error occurred.", e);
    }
}
