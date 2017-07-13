package chatbot.platforms.web.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by ramgathreya on 7/13/17.
 */
@Controller
public class AdminController {
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @RequestMapping(method= RequestMethod.GET, path="/admin")
    public String actionAdminGet() {
        return "admin";
    }
}
