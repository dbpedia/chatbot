package chatbot;

import chatbot.rivescript.RiveScriptBot;
import com.github.messenger4j.MessengerPlatform;
import com.github.messenger4j.send.MessengerSendClient;
import com.rivescript.RiveScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Created by ramgathreya on 5/10/17.
 * http://bits-and-kites.blogspot.com/2015/03/spring-and-nodejs.html
 */
@SpringBootApplication
public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    @Bean
    public RiveScriptBot initializeRiveScriptBot() {
        return new RiveScriptBot();
    }

    @Bean
    public MessengerSendClient initializeFBMessengerSendClient(@Value("${chatbot.fb.pageAccessToken}") String pageAccessToken) {
        logger.info("Initializing MessengerSendClient - pageAccessToken: {}", pageAccessToken);
        return MessengerPlatform.newSendClientBuilder(pageAccessToken).build();
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
