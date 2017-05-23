package chatbot.lib.response;

import java.util.ArrayList;

/**
 * Created by ramgathreya on 5/22/17.
 */
public class Response {
    private String messageType;
    private ArrayList<ResponseData> messageData = new ArrayList<>();

    public ArrayList<ResponseData> getMessageData() {
        return messageData;
    }

    public Response setMessageData(ArrayList<ResponseData> messageData) {
        this.messageData = messageData;
        return this;
    }

    public String getMessageType() {
        return messageType;
    }

    public Response setMessageType(String messageType) {
        this.messageType = messageType;
        return this;
    }

    public Response addData(ResponseData[] responseDatas) {
        for(ResponseData data : responseDatas) {
            this.addData(data);
        }
        return this;
    }

    public Response addData(ResponseData responseData) {
        this.messageData.add(responseData);
        return this;
    }
}
