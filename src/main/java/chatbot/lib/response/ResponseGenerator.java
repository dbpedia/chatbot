package chatbot.lib.response;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ramgathreya on 5/22/17.
 */
public class ResponseGenerator {
    private List<Response> response = new ArrayList<>();
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
        this.addResponse(new Response().setMessageType(ResponseType.CAROUSEL_MESSAGE)
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

    public List<Response> getResponse() {
        return this.response;
    }
}
