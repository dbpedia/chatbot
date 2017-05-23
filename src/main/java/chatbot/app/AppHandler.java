package chatbot.app;

import chatbot.lib.Platform;
import chatbot.lib.request.Request;
import chatbot.lib.request.RequestHandler;
import chatbot.lib.response.Response;
import chatbot.rivescript.RiveScriptBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody
    List<Response> handleRequest(@RequestBody final Request request) {
        request.setUserId("USER") // This needs to be sent from the client side
                .setPlatform(Platform.WEB);
        return (List<Response>) new RequestHandler(request, riveScriptBot).handleRequest();
    }
}
