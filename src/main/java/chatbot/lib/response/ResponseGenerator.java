package chatbot.lib.response;

import chatbot.lib.Utility;

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
        this.addResponse(new Response().setMessageType(ResponseType.TEXT)
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
        this.addResponse(new Response().setMessageType(ResponseType.BUTTON_TEXT)
                .addData(data)
        );
        return this;
    }

    public ResponseGenerator addCarouselResponse(ResponseData[] data) {
        this.addResponse(new Response().setMessageType(ResponseType.CAROUSEL)
                .addData(data)
        );
        return this;
    }

    public List<Response> getResponse() {
        return this.response;
    }
}
