package chatbot.platforms.slack;

import chatbot.Application;
import chatbot.lib.Colors;
import chatbot.lib.Utility;
import chatbot.lib.request.MessageData;
import chatbot.lib.request.Request;
import chatbot.lib.request.RequestRouter;
import chatbot.lib.request.RequestType;
import chatbot.lib.response.Response;
import chatbot.lib.response.ResponseData;
import chatbot.lib.response.ResponseType;
import chatbot.platforms.Platform;
import com.github.seratch.jslack.Slack;
import com.github.seratch.jslack.api.methods.SlackApiException;
import com.github.seratch.jslack.api.methods.request.auth.AuthTestRequest;
import com.github.seratch.jslack.api.methods.request.channels.ChannelsInfoRequest;
import com.github.seratch.jslack.api.methods.request.chat.ChatPostMessageRequest;
import com.github.seratch.jslack.api.methods.request.groups.GroupsInfoRequest;
import com.github.seratch.jslack.api.methods.response.auth.AuthTestResponse;
import com.github.seratch.jslack.api.methods.response.channels.ChannelsInfoResponse;
import com.github.seratch.jslack.api.methods.response.groups.GroupsInfoResponse;
import com.github.seratch.jslack.api.model.Action;
import com.github.seratch.jslack.api.model.Attachment;
import com.github.seratch.jslack.api.rtm.RTMClient;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ramgathreya on 7/8/17.
 * https://stackoverflow.com/questions/41111227/how-can-a-slack-bot-detect-a-direct-message-vs-a-message-in-a-channel
 */
@RestController
public class SlackHandler {
    private static final Logger logger = LoggerFactory.getLogger(SlackHandler.class);
    private static final int  MAX_TEXT_LENGTH = 750;

    private RTMClient rtmClient;
    private AuthTestResponse botData;
    private String slackToken;
    private Application.Helper helper;
    private Slack slack;

    @Value("${chatbot.baseUrl}")
    private String baseUrl;

    @Autowired
    public SlackHandler(@Value("${chatbot.slack.botToken}") final String slackToken, Application.Helper helper) {
        this.helper = helper;
        this.slackToken = slackToken;
        slack = Slack.getInstance();
        try {
            botData = Slack.getInstance().methods().authTest(
                    AuthTestRequest.builder().token(slackToken).build()
            );
            String botUser = botData.getUserId();
            rtmClient = new Slack().rtm(slackToken);
            rtmClient.addMessageHandler((message) -> {
                JsonObject json = new JsonParser().parse(message).getAsJsonObject();
                if (json.get("type").getAsString().equals("message")) {
                    try {
                        String channel = json.get("channel").getAsString();
                        String user = json.get("user").getAsString();
                        String text = json.get("text").getAsString();

                        // User sending the message should not be bot
                        if(!user.equals(botUser)) {
                            // Direct Mention
                            if (text.contains("<@" + botUser + ">")) {
                                handleTextRequest(user, channel, text);
                            }
                            else {
                                ChannelsInfoResponse channelsResponse = slack.methods().channelsInfo(
                                        ChannelsInfoRequest.builder().token(slackToken).channel(channel).build()
                                );

                                GroupsInfoResponse groupsResponse = slack.methods().groupsInfo(
                                        GroupsInfoRequest.builder().token(slackToken).channel(channel).build()
                                );

                                // Direct Message
                                if (!channelsResponse.isOk() && !groupsResponse.isOk()) {
                                    handleTextRequest(user, channel, text);
                                }
                            }
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    } catch (SlackApiException e) {
                        e.printStackTrace();
                    }
                }
            });
            rtmClient.connect();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleTextRequest(String user, String channel, String message) {
        try {
            message = message.replaceAll("<@.*>", "").trim();
            Request request = new Request(user, RequestType.TEXT_MESSAGE, Platform.SLACK)
                    .setMessageData(new ArrayList<>(
                            Arrays.asList(new MessageData().setText(message))
                    ));
            List<Response> responseList = new RequestRouter(request, helper).routeRequest();
            sendResponse(channel, responseList);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    @RequestMapping(value = "/slackwebhook", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Void> handleRequest(@RequestBody final MultiValueMap response) {
        try {
            JsonNode json = new ObjectMapper().readTree(response.get("payload").toString()).get(0);
            String payload = json.get("actions").get(0).get("value").asText();
            String channel = json.get("channel").get("id").asText();
            String user = json.get("user").get("id").asText();

            new Thread(){
                public void run() {
                    try {
                        Request request = new Request(user, RequestType.PARAMETER_MESSAGE, Platform.SLACK)
                                .setMessageData(new ArrayList<>(
                                        Arrays.asList(new MessageData().setPayload(payload))
                                ));
                        List<Response> responseList = new RequestRouter(request, helper).routeRequest();
                        sendResponse(channel, responseList);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    private void sendResponse(String channel, List<Response> responseList) {
        try{
            for(Response response : responseList) {
                switch (response.getMessageType()) {
                    case ResponseType.TEXT_MESSAGE:
                        sendTextMessage(channel, response.getMessageData().get(0).getText());
                        break;
                    case ResponseType.GENERIC_MESSAGE:
                        sendGenericMessage(channel, response.getMessageData());
                        break;
                    case ResponseType.BUTTON_TEXT_MESSAGE:
                    case ResponseType.SMART_REPLY_MESSAGE:
                        sendGenericMessage(channel, response.getMessageData());
                        break;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendTextMessage(String channel, String message) {
        try {
            Slack.getInstance().methods().chatPostMessage(
                    ChatPostMessageRequest.builder()
                            .token(slackToken)
                            .channel(channel)
                            .username(botData.getUser())
                            .asUser(true)
                            .text(message)
                            .build()
            );
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String processText(String text) {
        if(text.length() <= MAX_TEXT_LENGTH) {
            return text;
        }
        return text.substring(0, MAX_TEXT_LENGTH - 3) + "...";
    }

    private void sendGenericMessage(String channel, List<ResponseData> responseDatas) {
        try {
            List<com.github.seratch.jslack.api.model.Attachment> attachments = new ArrayList<>();
            for (ResponseData data : responseDatas) {
                Attachment.AttachmentBuilder attachmentBuilder = Attachment.builder();
                attachmentBuilder.title(data.getTitle()).color(Colors.PRIMARY);
                String footer = "";

                if (data.getImage() != null) {
                    attachmentBuilder.thumbUrl(Utility.generateImageUrl(baseUrl, data.getImage()));
                }

                if (data.getText() != null) {
                    attachmentBuilder.text(processText(data.getText()));
                }

                attachmentBuilder.callbackId("dummy");

                List<Action> actions = new ArrayList();
                if (data.getButtons().size() > 0) {
                    for(ResponseData.Button button : data.getButtons()) {
                        switch (button.getButtonType()) {
                            case ResponseType.BUTTON_LINK:
                                footer += "<" + button.getUri() + "|" + button.getTitle() + ">\n";
                                break;
                            case ResponseType.BUTTON_PARAM:
                                actions.add(Action.builder().style(button.getSlackStyle()).name("dummy").type("button").text(button.getTitle()).value(button.getUri()).build());
                                break;
                        }
                    }
                    attachmentBuilder.actions(actions);
                    attachmentBuilder.footer(footer);
                }
                else if(data.getSmartReplies().size() > 0) {
                    for(ResponseData.SmartReply smartReply : data.getSmartReplies()) {
                        actions.add(Action.builder().style(smartReply.getSlackStyle()).name("dummy").type("button").text(smartReply.getTitle()).value(smartReply.getUri()).build());
                    }
                }
                attachmentBuilder.actions(actions);
                attachments.add(attachmentBuilder.build());
            }
            slack.methods().chatPostMessage(
                    ChatPostMessageRequest.builder()
                            .token(slackToken)
                            .channel(channel)
                            .username(botData.getUser())
                            .asUser(true)
                            .attachments(attachments)
                            .build()
            );
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void destroy() throws IOException {
        logger.info("coems here to disconnect");
        rtmClient.disconnect();
    }
}