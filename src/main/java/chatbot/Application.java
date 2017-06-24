package chatbot;

import chatbot.couchbase.ChatRepository;
import chatbot.rivescript.RiveScriptBot;
import com.github.messenger4j.MessengerPlatform;
import com.github.messenger4j.send.MessengerSendClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.couchbase.config.AbstractCouchbaseConfiguration;
import org.springframework.data.couchbase.repository.config.EnableCouchbaseRepositories;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.Arrays;
import java.util.List;

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

    @Bean
    public ChatRepository initializeChatRepository() {
        return new ChatRepository();
    }
//    @Configuration
//    @EnableCouchbaseRepositories
//    static class CouchbaseConfiguration extends AbstractCouchbaseConfiguration {
//        @Value("${couchbase.cluster.bucket}")
//        private String bucket;
//        @Value("${couchbase.cluster.password}")
//        private String password;
//        @Value("${couchbase.cluster.host}")
//        private String host;
//
//        @Override
//        protected List<String> getBootstrapHosts() {
//            return Arrays.asList(host);
//        }
//
//        @Override
//        protected String getBucketName() {
//            return bucket;
//        }
//
//        @Override
//        protected String getBucketPassword() {
//            return password;
//        }
//    }

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

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
