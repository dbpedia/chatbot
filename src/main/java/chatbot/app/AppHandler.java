package chatbot.app;

import chatbot.rivescript.RiveScriptBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Created by ramgathreya on 5/19/17.
 */
@RestController
@RequestMapping("/webhook")
public class AppHandler {

    private static final Logger logger = LoggerFactory.getLogger(AppHandler.class);
    private final RiveScriptBot riveScriptBot;

    @Autowired
    AppHandler(final RiveScriptBot riveScriptBot) {
        this.riveScriptBot = riveScriptBot;
    }

    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody String handleRequest(@RequestBody final Request messageData) {
        String response = "";

        switch(messageData.getMessageType()) {
            case RequestType.TEXT_MESSAGE:
                response = this.riveScriptBot.reply("USER", messageData.getMessage()); // need to use a unique identifier instead
                break;
        }
        return response;
    }
}
