package chatbot;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by ramgathreya on 5/10/17.
 */

@RestController
public class GreetingController {

    private static final String template = "Hello %s";
    private static final AtomicLong counter = new AtomicLong();

    @RequestMapping(method=GET, path="/greeting")
    public Greeting greeting(@RequestParam(value="name", defaultValue="World") String name) {
        return new Greeting(counter.incrementAndGet(), String.format(template, name));
    }
}
