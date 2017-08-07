package chatbot.platforms.web.controllers;

import chatbot.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by ramgathreya on 8/6/17.
 */
@Controller
public class EmbedController {
    private static final Logger logger = LoggerFactory.getLogger(EmbedController.class);
    private Application.Helper helper;

    @Autowired
    public EmbedController(final Application.Helper helper) {
        this.helper = helper;
    }

    @RequestMapping(method= RequestMethod.GET, path="/embed")
    public String actionEmbedGet() {
        return "embed";
    }
}
