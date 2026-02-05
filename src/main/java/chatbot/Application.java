package chatbot;

import chatbot.cache.WolframRepository;
import chatbot.lib.api.SPARQL;
import chatbot.rivescript.RiveScriptBot;
import codeanticode.eliza.ElizaMain;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.github.messenger4j.MessengerPlatform;
import com.github.messenger4j.send.MessengerSendClient;
import org.languagetool.JLanguageTool;
import org.languagetool.language.AmericanEnglish;
import org.languagetool.language.BritishEnglish;
import org.languagetool.rules.Rule;
import org.languagetool.rules.spelling.SpellingCheckRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by ramgathreya on 5/10/17.
 * http://bits-and-kites.blogspot.com/2015/03/spring-and-nodejs.html
 */
@EnableCaching
@SpringBootApplication
public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    @Bean
    public MessengerSendClient initializeFBMessengerSendClient(
            @Value("${chatbot.fb.pageAccessToken}") String pageAccessToken) {
        logger.info("Initializing MessengerSendClient - pageAccessToken: {}", pageAccessToken);
        return MessengerPlatform.newSendClientBuilder(pageAccessToken).build();
    }

    @Configuration
    static class AssetsConfiguration extends WebMvcConfigurerAdapter {
        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            registry.addResourceHandler("/assets/**").addResourceLocations("file:node_modules/")
                    .setCacheControl(CacheControl.maxAge(1, TimeUnit.DAYS));
            registry.addResourceHandler("/js/**").addResourceLocations("file:src/main/app/js/")
                    .setCacheControl(CacheControl.maxAge(1, TimeUnit.DAYS));
            registry.addResourceHandler("/css/**").addResourceLocations("file:src/main/app/css/")
                    .setCacheControl(CacheControl.maxAge(1, TimeUnit.DAYS));
            registry.addResourceHandler("/images/**").addResourceLocations("file:src/main/resources/static/images/")
                    .setCacheControl(CacheControl.maxAge(1, TimeUnit.DAYS));
            super.addResourceHandlers(registry);
        }
    }

    @Configuration
    @EnableWebSecurity
    static class WebSecurityConfig extends WebSecurityConfigurerAdapter {

        protected static String baseUrl;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .csrf().disable()
                    .authorizeRequests()
                    .antMatchers("/", "/assets/**/*", "/js/*", "/images/**/*", "/feedback", "/webhook", "/fbwebhook",
                            "/slackwebhook", "/embed")
                    .permitAll()
                    .anyRequest().authenticated()
                    .and()
                    .formLogin()
                    .defaultSuccessUrl("/admin")
                    .loginPage("/login")
                    .permitAll()
                    .and()
                    .logout()
                    .permitAll();
            http.headers().frameOptions().disable();
        }

        @Autowired
        protected void configureGlobal(AuthenticationManagerBuilder auth, @Value("${admin.username}") String username,
                @Value("${admin.password}") String password, @Value("${chatbot.baseUrl}") String baseUrl)
                throws Exception {
            this.baseUrl = baseUrl;
            auth
                    .inMemoryAuthentication()
                    .withUser(username).password(password).roles("ADMIN");
        }
    }

    @Component
    public static class Helper {
        private RiveScriptBot riveScriptBot;
        private Database chatDB = null, feedbackDB = null, explorerDB = null;
        private ElizaMain eliza;
        private WolframRepository wolframRepository;
        private String tmdbApiKey;
        private SPARQL sparql;
        private JLanguageTool languageTool;

        @Autowired
        public Helper(final CloudantClient cloudantClient,
                WolframRepository wolframRepository,
                @Value("${cloudant.chatDB}") String chatDBName,
                @Value("${cloudant.feedbackDB}") String feedbackDBName,
                @Value("${cloudant.explorerDB}") String explorerDBName,
                @Value("${tmdb.apiKey}") String tmdbApiKey) {
            // Initialize each database independently so partial successes are possible
            try {
                chatDB = cloudantClient.database(chatDBName, true);
            } catch (Exception e) {
                logger.error("Failed to open chatDB: {}", chatDBName, e);
            }

            try {
                feedbackDB = cloudantClient.database(feedbackDBName, true);
            } catch (Exception e) {
                logger.error("Failed to open feedbackDB: {}", feedbackDBName, e);
            }

            try {
                explorerDB = cloudantClient.database(explorerDBName, true);
            } catch (Exception e) {
                logger.error("Failed to open explorerDB: {}", explorerDBName, e);
            }

            // Initialize remaining components
            this.tmdbApiKey = tmdbApiKey;
            this.wolframRepository = wolframRepository;

            riveScriptBot = new RiveScriptBot();
            eliza = new ElizaMain();
            eliza.readScript(true, "src/main/resources/eliza/script");

            // Use disabled SPARQL instance if explorerDB is unavailable to prevent NPE
            sparql = (explorerDB != null)
                    ? new SPARQL(explorerDB)
                    : SPARQL.disabled();

            languageTool = new JLanguageTool(new AmericanEnglish());
            for (Rule rule : languageTool.getAllActiveRules()) {
                if (rule instanceof SpellingCheckRule) {
                    List<String> wordsToIgnore = Arrays.asList(new String[] { "nlp", "merkel" });
                    ((SpellingCheckRule) rule).addIgnoreTokens(wordsToIgnore);
                }
            }
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

        public ElizaMain getEliza() {
            return eliza;
        }

        public WolframRepository getWolframRepository() {
            return wolframRepository;
        }

        public String getTmdbApiKey() {
            return tmdbApiKey;
        }

        public Database getExplorerDB() {
            return explorerDB;
        }

        public boolean isExplorerDBAvailable() {
            return explorerDB != null;
        }

        public SPARQL getSparql() {
            return sparql;
        }

        public JLanguageTool getLanguageTool() {
            return languageTool;
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
