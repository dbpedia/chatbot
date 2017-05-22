package chatbot.app.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by ramgathreya on 5/12/17.
 */
@Controller
public class IndexController {

    @RequestMapping(method=GET, path="/")
    public String actionIndex() {
        return "index";
    }
}
