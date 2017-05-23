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

    public ResponseGenerator addTextResponse(ResponseData data) {
        this.addResponse(new Response().setMessageType(ResponseType.TEXT)
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
