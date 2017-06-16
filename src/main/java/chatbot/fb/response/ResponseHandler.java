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
import com.github.messenger4j.send.templates.ButtonTemplate;
import com.github.messenger4j.send.templates.GenericTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
                case ResponseType.TEXT_MESSAGE:
                    sendTextMessage(request.getUserId(), response.getMessageData().get(0).getText());
                    break;
                case ResponseType.BUTTON_TEXT_MESSAGE:
                    sendButtonMessage(request.getUserId(), response.getMessageData().get(0));
                    break;
                case ResponseType.CAROUSEL_MESSAGE:
                    sendGenericMessage(request.getUserId(), response.getMessageData());
                    break;
                default: // This case needs to be separately handled probably send a text message or something saying the bot could not understand the query
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

    private void sendButtonMessage(String recipientId, ResponseData data) throws MessengerApiException, MessengerIOException {
        Button.ListBuilder buttons = Button.newListBuilder();
        for(ResponseData.ButtonData button : data.getButtons()) {
            switch(button.getButtonType()) {
                case ResponseType.BUTTON_LINK:
                    buttons.addUrlButton(button.getTitle(), button.getUri()).toList();
                break;
                case ResponseType.BUTTON_PARAM:
                    buttons.addPostbackButton(button.getTitle(), button.getUri()).toList();
                break;
            }
        }

        ButtonTemplate buttonTemplate = ButtonTemplate.newBuilder(data.getText(), buttons.build()).build();
        sendClient.sendTemplate(recipientId, buttonTemplate);
    }


    private String processSubtitle(String subtitle) {
        if(subtitle.length() <= MAX_SUBTITLE_LENGTH) {
            return subtitle;
        }
        return subtitle.substring(0, MAX_SUBTITLE_LENGTH - 3) + "...";
    }

    private void sendGenericMessage(String recipientId, List<ResponseData> items) throws MessengerApiException, MessengerIOException {
        GenericTemplate.Element.ListBuilder genericTemplate = GenericTemplate.newBuilder().addElements();

        for(ResponseData item : items) {
            GenericTemplate.Element.Builder element = genericTemplate.addElement(item.getTitle());

            if (item.getImage() != null) {
                element.imageUrl(Utility.generateImageUrl(baseUrl, item.getImage()));
            }

            if (item.getText() != null) {
                element.subtitle(processSubtitle(item.getText()));
            }

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
    }

    private void handleSendException(Exception e) {
        logger.error("Message could not be sent. An unexpected error occurred.", e);
    }
}
