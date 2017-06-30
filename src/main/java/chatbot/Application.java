package chatbot;

import chatbot.rivescript.RiveScriptBot;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.github.messenger4j.MessengerPlatform;
import com.github.messenger4j.send.MessengerSendClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by ramgathreya on 5/10/17.
 * http://bits-and-kites.blogspot.com/2015/03/spring-and-nodejs.html
 */
@SpringBootApplication
public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    @Bean
    public MessengerSendClient initializeFBMessengerSendClient(@Value("${chatbot.fb.pageAccessToken}") String pageAccessToken) {
        logger.info("Initializing MessengerSendClient - pageAccessToken: {}", pageAccessToken);
        return MessengerPlatform.newSendClientBuilder(pageAccessToken).build();
    }

    @Configuration
    static class AssetsConfiguration extends WebMvcConfigurerAdapter {
        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            registry.addResourceHandler("/assets/**").addResourceLocations("file:node_modules/");
            registry.addResourceHandler("/js/**").addResourceLocations("file:src/main/app/js/");
            registry.addResourceHandler("/css/**").addResourceLocations("file:src/main/app/css/");
            super.addResourceHandlers(registry);
        }
    }

    @Component
    public static class Helper {
        private RiveScriptBot riveScriptBot;
        private Database chatDB, feedbackDB;

        @Autowired
        public Helper(final CloudantClient cloudantClient, @Value("${cloudant.chatDB}") String chatDBName, @Value("${cloudant.feedbackDB}") String feedbackDBName) {
            riveScriptBot = new RiveScriptBot();
            chatDB = cloudantClient.database(chatDBName, true);
            feedbackDB = cloudantClient.database(feedbackDBName, true);
        }

        public RiveScriptBot getRiveScriptBot() {
            return riveScriptBot;
        }

        public Database getChatDB() {
            return chatDB;
        }

        public Database getFeedbackDB() {
            return feedbackDB;
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
