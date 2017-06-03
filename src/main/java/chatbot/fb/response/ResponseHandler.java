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

    private void sendGenericMessage(String recipientId, List<ResponseData> items) throws MessengerApiException, MessengerIOException {
//        final List<Button> riftButtons = Button.newListBuilder()
//                .addUrlButton("Open Web URL", "https://www.oculus.com/en-us/rift/").toList()
//                .addPostbackButton("Call Postback", "Payload for first bubble").toList()
//                .build();
//
//        final List<Button> touchButtons = Button.newListBuilder()
//                .addUrlButton("Open Web URL", "https://www.oculus.com/en-us/touch/").toList()
//                .addPostbackButton("Call Postback", "Payload for second bubble").toList()
//                .build();

        GenericTemplate.Element.ListBuilder genericTemplate = GenericTemplate.newBuilder().addElements();


        for(ResponseData item : items) {
            logger.info("IMAGE URL: " + Utility.generateImageUrl(baseUrl, item.getImage()));

            genericTemplate.addElement(item.getTitle())
                .imageUrl(Utility.generateImageUrl(baseUrl, item.getImage()))
                .subtitle(item.getText())
                .toList();
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
