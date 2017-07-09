package chatbot.lib.response;

import chatbot.lib.Colors;
import chatbot.lib.Utility;
import chatbot.lib.request.TemplateType;
import chatbot.lib.request.Request;
import chatbot.platforms.Platform;
import chatbot.rivescript.RiveScriptBot;
import chatbot.rivescript.RiveScriptReplyType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ramgathreya on 5/22/17.
 */
public class ResponseGenerator {
    private List<Response> response = new ArrayList<>();
    private boolean showFeedback = true;

    public ResponseGenerator addResponse(Response response) {
        this.response.add(response);
        return this;
    }

    public ResponseGenerator addResponses(List<Response> responses) {
        this.response.addAll(responses);
        return this;
    }

    public ResponseGenerator addTextResponse(ResponseData data) {
        this.addResponse(new Response().setMessageType(ResponseType.TEXT_MESSAGE)
                .addData(data)
        );
        return this;
    }

    // When text response is an array of strings
    public ResponseGenerator addTextResponses(String[] textResponses) {
        for(String reply : textResponses) {
            this.addTextResponse(new ResponseData(reply));
        }
        return this;
    }

    public ResponseGenerator addButtonTextResponse(ResponseData data) {
        this.addResponse(new Response().setMessageType(ResponseType.BUTTON_TEXT_MESSAGE)
                .addData(data)
        );
        return this;
    }

    public ResponseGenerator addCarouselResponse(ResponseData[] data) {
        this.addResponse(new Response().setMessageType(ResponseType.GENERIC_MESSAGE)
                .addData(data)
        );
        return this;
    }

    // Convenience function converts arraylist to array
    public ResponseGenerator addCarouselResponse(List<ResponseData> data) {
        return addCarouselResponse(data.toArray(new ResponseData[data.size()]));
    }

    public ResponseGenerator addSmartReplyResponse(ResponseData data) {
        this.addResponse(new Response().setMessageType(ResponseType.SMART_REPLY_MESSAGE)
                .addData(data)
        );
        return this;
    }

    public ResponseGenerator addFeedbackResponse(String msgId) {
        if (showFeedback) {
            Response response = this.response.get(this.response.size() - 1);

            // Show feedback message when the response does not contain a smart reply
            if(!response.getMessageType().equals(ResponseType.SMART_REPLY_MESSAGE)) {
                addSmartReplyResponse(new ResponseData()
                        .setText("Was this helpful?")
                        .addSmartReply(new ResponseData.SmartReply("Yes", TemplateType.FEEDBACK + Utility.STRING_SEPARATOR + TemplateType.YES + Utility.STRING_SEPARATOR + msgId).setSlackStyle(Platform.SLACK_BUTTON_PRIMARY))
                        .addSmartReply(new ResponseData.SmartReply("No", TemplateType.FEEDBACK + Utility.STRING_SEPARATOR + TemplateType.NO + Utility.STRING_SEPARATOR + msgId).setSlackStyle(Platform.SLACK_BUTTON_DANGER))
                );
            }

        }
        return this;
    }

    public ResponseGenerator setShowFeedback(boolean showFeedback) {
        this.showFeedback = showFeedback;
        return this;
    }

    public ResponseGenerator setFallbackResponse(Request request, RiveScriptBot riveScriptBot) {
        showFeedback = false;
        addTextResponse(new ResponseData(riveScriptBot.answer(request.getUserId(), RiveScriptReplyType.FALLBACK_TEXT)[0]));
        return this;
    }

    public ResponseGenerator setNoResultsResponse(Request request, RiveScriptBot riveScriptBot) {
        showFeedback = false;
        addTextResponse(new ResponseData(riveScriptBot.answer(request.getUserId(), RiveScriptReplyType.NO_RESULTS_TEXT)[0]));
        return this;
    }

    public List<Response> getResponse() {
        return this.response;
    }
}
