package chatbot.fb;

import chatbot.Application;
import chatbot.couchbase.ChatRepository;
import chatbot.fb.response.ResponseHandler;
import chatbot.lib.Platform;
import chatbot.lib.request.*;
import chatbot.lib.response.Response;
import chatbot.rivescript.RiveScriptBot;
import com.github.messenger4j.MessengerPlatform;
import com.github.messenger4j.exceptions.MessengerApiException;
import com.github.messenger4j.exceptions.MessengerIOException;
import com.github.messenger4j.exceptions.MessengerVerificationException;
import com.github.messenger4j.receive.MessengerReceiveClient;
import com.github.messenger4j.receive.events.QuickReplyMessageEvent;
import com.github.messenger4j.receive.events.TextMessageEvent;
import com.github.messenger4j.receive.handlers.PostbackEventHandler;
import com.github.messenger4j.receive.handlers.QuickReplyMessageEventHandler;
import com.github.messenger4j.receive.handlers.TextMessageEventHandler;
import com.github.messenger4j.send.MessengerSendClient;
import com.github.messenger4j.send.SenderAction;
import com.github.messenger4j.setup.CallToAction;
import com.github.messenger4j.setup.CallToActionType;
import com.github.messenger4j.setup.MessengerSetupClient;
import com.github.messenger4j.user.UserProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by ramgathreya on 5/10/17.
 */
@RestController
@RequestMapping("/fbwebhook")
public class FBHandler {
    private static final Logger logger = LoggerFactory.getLogger(FBHandler.class);
    private final MessengerReceiveClient receiveClient;
    private final MessengerSendClient sendClient;
    private final Application.Helper helper;

    private final String appSecret;
    private final String verifyToken;
    private final String pageAccessToken;

    private final String baseUrl;

    @Autowired
    FBHandler(@Value("${chatbot.fb.appSecret}") final String appSecret,
              @Value("${chatbot.fb.verifyToken}") final String verifyToken,
              @Value("${chatbot.fb.pageAccessToken}") final String pageAccessToken,
              @Value("${chatbot.baseUrl}") final String baseUrl,
              final MessengerSendClient sendClient,
              final Application.Helper helper) throws MessengerApiException, MessengerIOException, MalformedURLException {
        logger.debug("App Secret is " + appSecret);
        logger.debug("Verification Token is " + verifyToken);

        this.appSecret = appSecret;
        this.verifyToken = verifyToken;
        this.pageAccessToken = pageAccessToken;
        this.baseUrl = baseUrl;

        this.receiveClient = MessengerPlatform.newReceiveClientBuilder(this.appSecret, this.verifyToken)
                .onTextMessageEvent(textMessageEventHandler())
                .onPostbackEvent(postbackEventHandler())
                .onQuickReplyMessageEvent(quickReplyMessageEventHandler())
                .build();

        this.sendClient = sendClient;
        this.helper = helper;

        MessengerSetupClient setupClient = MessengerPlatform.newSetupClientBuilder(this.pageAccessToken).build();

        setupClient.setupStartButton(ParameterType.START);
        setupClient.setupPersistentMenu(new ArrayList<>(Arrays.asList(
                new CallToAction.Builder().type(CallToActionType.POSTBACK).title("Start Over").payload(ParameterType.START).build(),
                new CallToAction.Builder().type(CallToActionType.WEB_URL).title("View on Web").url(new URL("http://www.dbpedia.org")).build()
        )));
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<String> verifyWebHook(@RequestParam(MessengerPlatform.MODE_REQUEST_PARAM_NAME) final String mode,
                                                @RequestParam(MessengerPlatform.VERIFY_TOKEN_REQUEST_PARAM_NAME) final String verifyToken,
                                                @RequestParam(MessengerPlatform.CHALLENGE_REQUEST_PARAM_NAME) final String challenge) {

        logger.debug("Received Webhook verification request - mode: {} | verifyToken: {} | challenge: {}", mode, verifyToken, challenge);
        try {
            return ResponseEntity.ok(this.receiveClient.verifyWebhook(mode, verifyToken, challenge));
        } catch (MessengerVerificationException e) {
            logger.warn("Webhook verification failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Void> handleRequest(@RequestBody final String payload,
                                               @RequestHeader(MessengerPlatform.SIGNATURE_HEADER_NAME) final String signature) {
        logger.info("Received Messenger Platform cFallback - payload: {} | signature: {}", payload, signature);
        try {
            this.receiveClient.processCallbackPayload(payload, signature);
            logger.debug("Processed callback payload successfully");
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        catch (MessengerVerificationException e) {
            logger.warn("Processing of callback payload failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    private UserProfile getGraphData(String userId) throws MessengerApiException, MessengerIOException {
        return MessengerPlatform.newUserProfileClientBuilder(this.pageAccessToken)
                .build()
                .queryUserProfile(userId);
    }

    private String getFirstName(String userId) throws MessengerApiException, MessengerIOException {
        return getGraphData(userId).getFirstName();
    }

    private QuickReplyMessageEventHandler quickReplyMessageEventHandler() {
        return event -> {
            final String senderId = event.getSender().getId();
            final String payload = event.getQuickReply().getPayload();

            try {
                Request request = new Request(senderId, RequestType.PARAMETER_MESSAGE, Platform.FB)
                        .setMessageData(new ArrayList<>(
                                Arrays.asList(new MessageData().setPayload(payload))
                        ));
                List<Response> responseList = new RequestRouter(request, helper).routeRequest();
                new ResponseHandler(request, sendClient, responseList, baseUrl).generateResponse();
            } catch (MessengerApiException e) {
                e.printStackTrace();
            } catch (MessengerIOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }

    private PostbackEventHandler postbackEventHandler() {
        return event -> {
            final String senderId = event.getSender().getId();
            final String payload = event.getPayload();

            try {
                sendReadReceipt(senderId);
                sendTypingOn(senderId);

                Request request = new Request(senderId, RequestType.PARAMETER_MESSAGE, Platform.FB)
                        .setMessageData(new ArrayList<>(
                                Arrays.asList(new MessageData().setPayload(payload))
                        ));
                List<Response> responseList = new RequestRouter(request, helper).routeRequest();
                new ResponseHandler(request, sendClient, responseList, baseUrl).generateResponse();

                sendTypingOff(senderId);
            } catch (MessengerApiException e) {
                e.printStackTrace();
            } catch (MessengerIOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }

    private TextMessageEventHandler textMessageEventHandler() {
        return (TextMessageEvent event) -> {
            logger.debug("Received TextMessageEvent: {}", event);

            final String messageId = event.getMid();
            final String messageText = event.getText();
            final String senderId = event.getSender().getId();
            final Date timestamp = event.getTimestamp();

            logger.info("Received message '{}' with text '{}' from user '{}' at '{}'", messageId, messageText, senderId, timestamp);

            try {
                sendReadReceipt(senderId);
                sendTypingOn(senderId);

                Request request = new Request(senderId, RequestType.TEXT_MESSAGE, Platform.FB) // Creating Request Object
                        .setMessageData(new ArrayList<>(
                                Arrays.asList(new MessageData().setText(messageText))
                        ));
                List<Response> responseList = new RequestRouter(request, helper).routeRequest(); // Get Generic Response List
                new ResponseHandler(request, sendClient, responseList, baseUrl).generateResponse(); // Translate for FB

                sendTypingOff(senderId);
            } catch (MessengerApiException | MessengerIOException e) {
                handleSendException(e);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }

    private void sendReadReceipt(String recipientId) throws MessengerApiException, MessengerIOException {
        sendClient.sendSenderAction(recipientId, SenderAction.MARK_SEEN);
    }

    private void sendTypingOn(String recipientId) throws MessengerApiException, MessengerIOException {
        sendClient.sendSenderAction(recipientId, SenderAction.TYPING_ON);
    }

    private void sendTypingOff(String recipientId) throws MessengerApiException, MessengerIOException {
        sendClient.sendSenderAction(recipientId, SenderAction.TYPING_OFF);
    }

    private void handleSendException(Exception e) {
        logger.error("Message could not be sent. An unexpected error occurred.", e);
    }

    private void handleIOException(Exception e) {
        logger.error("Could not open Spring.io page. An unexpected error occurred.", e);
    }
}
