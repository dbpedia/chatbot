package chatbot.platforms.web.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by ramgathreya on 5/12/17.
 */
@Controller
public class IndexController {
    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @RequestMapping(method=GET, path="/")
    public ModelAndView actionIndex(@Value("${chatbot.gaID}") String gaID) {
        ModelAndView model = new ModelAndView("index");
        model.addObject("gaID", gaID);
        return model;
    }
}
