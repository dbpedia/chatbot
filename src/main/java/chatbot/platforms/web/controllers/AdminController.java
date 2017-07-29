package chatbot.platforms.web.controllers;

import chatbot.Application;
import chatbot.lib.request.Request;
import chatbot.lib.response.Response;
import com.cloudant.client.api.views.Key;
import com.cloudant.client.api.views.ViewRequest;
import com.cloudant.client.api.views.ViewResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ramgathreya on 7/13/17.
 */
@Controller
public class AdminController {
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    private Application.Helper helper;

    private static final int MAX_SIZE = 10;

    @Autowired
    public AdminController(final Application.Helper helper) {
        this.helper = helper;
    }

    @RequestMapping(method=RequestMethod.GET, path="/admin")
    public String actionAdminGet() {
        return "admin";
    }

    @RequestMapping(method=RequestMethod.POST, path="/admin/user-list", produces = "application/json")
    public @ResponseBody List<UserList> actionUserList(@RequestParam String page) {
        try {
            return helper.getChatDB().getViewRequestBuilder("chats", "getUserList")
                .newRequest(Key.Type.COMPLEX, UserList.class)
                .startKey(Key.complex("\ufff0"))
                .endKey(Key.complex(""))
                .limit(MAX_SIZE)
                .skip((Integer.parseInt(page) - 1) * MAX_SIZE)
                .reduce(true)
                .descending(true)
                .group(true)
                .groupLevel(1)
                .build().getResponse().getValues();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<UserList>();
    }

    @RequestMapping(method=RequestMethod.POST, path="/admin/chat-list", produces = "application/json")
    public @ResponseBody List<ChatList> actionChatList(@RequestParam String userId, @RequestParam String page) {
        try {
            return helper.getChatDB().getViewRequestBuilder("chats", "getChatList")
                    .newRequest(Key.Type.COMPLEX, ChatList.class)
                    .startKey(Key.complex(userId))
                    .endKey(Key.complex(userId, "\ufff0"))
                    .inclusiveEnd(true)
                    .limit(MAX_SIZE)
                    .skip((Integer.parseInt(page) - 1) * MAX_SIZE)
                    .build().getResponse().getValues();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<ChatList>();
    }

    class ChatList {
        private String userId;
        private String timestamp;
        private boolean fromBot;
        private Request request;
        private List<Response> response;

        public String getUserId() {
            return userId;
        }

        public ChatList setUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public ChatList setTimestamp(String timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public boolean isFromBot() {
            return fromBot;
        }

        public ChatList setFromBot(boolean fromBot) {
            this.fromBot = fromBot;
            return this;
        }

        public Request getRequest() {
            return request;
        }

        public ChatList setRequest(Request request) {
            this.request = request;
            return this;
        }

        public List<Response> getResponse() {
            return response;
        }

        public ChatList setResponse(List<Response> response) {
            this.response = response;
            return this;
        }
    }

    class UserList {
        private String userId;
        private String count;
        private String last_timestamp;
        private String platform;

        public UserList(String userId, String count, String last_timestamp, String platform) {
            this.userId = userId;
            this.count = count;
            this.last_timestamp = last_timestamp;
            this.platform = platform;
        }

        public String getCount() {
            return count;
        }

        public UserList setCount(String count) {
            this.count = count;
            return this;
        }

        public String getLast_timestamp() {
            return last_timestamp;
        }

        public UserList setLast_timestamp(String last_timestamp) {
            this.last_timestamp = last_timestamp;
            return this;
        }

        public String getPlatform() {
            return platform;
        }

        public UserList setPlatform(String platform) {
            this.platform = platform;
            return this;
        }

        public String getUserId() {
            return userId;
        }

        public UserList setUserId(String userId) {
            this.userId = userId;
            return this;
        }
    }
}
