package chatbot.platforms.web.controllers;

import chatbot.Application;
import chatbot.couchdb.FeedbackModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by ramgathreya on 6/30/17.
 */
@Controller
public class FeedbackController {
    private static final Logger logger = LoggerFactory.getLogger(FeedbackController.class);
    private final Application.Helper helper;

    @Autowired
    FeedbackController(final Application.Helper helper) {
        this.helper = helper;
    }

    @RequestMapping(method=RequestMethod.POST, path="/feedback")
    public ResponseEntity<Void> actionFeedbackPost(@RequestBody FeedbackModel feedbackModel) {
        if(feedbackModel.validate()) {
            try {
                helper.getFeedbackDB().save(feedbackModel);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @RequestMapping(method=RequestMethod.GET, path="/feedback")
    public String actionFeedbackGet() {
        return "feedback";
    }
}
