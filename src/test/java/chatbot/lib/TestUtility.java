package chatbot.lib;

import chatbot.Application;
import chatbot.cache.WolframRepository;
import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;

import java.io.FileInputStream;
import java.net.URL;
import java.util.Properties;

import static junit.framework.TestCase.assertTrue;

/**
 * Created by ramgathreya on 6/20/17.
 */
public class TestUtility {
    public static void checkUrl(String url) {
        assertTrue("Check If URL is Proper", url.matches("^(((http|https)://)|mailto:).*$"));
    }

    public static Application.Helper getHelper() {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream("src/main/resources/application.properties"));

            CloudantClient cloudantClient = ClientBuilder
                    .url(new URL(properties.getProperty("cloudant.url")))
                    .username(properties.getProperty("cloudant.username"))
                    .password(properties.getProperty("cloudant.password"))
                    .build();
            WolframRepository wolframRepository = new WolframRepository(properties.getProperty("wolfram.apiKey"));
            return new Application.Helper(
                    cloudantClient,
                    wolframRepository,
                    properties.getProperty("cloudant.chatDB"),
                    properties.getProperty("cloudant.feedbackDB"),
                    properties.getProperty("cloudant.explorerDB"),
                    properties.getProperty("tmdb.apiKey")
            );
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
}
