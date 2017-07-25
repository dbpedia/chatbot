package chatbot.lib;

import chatbot.Application;
import chatbot.cache.WolframRepository;
import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import org.junit.Ignore;

import java.io.FileInputStream;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

import static junit.framework.TestCase.assertTrue;

/**
 * Created by ramgathreya on 6/20/17.
 */
@Ignore
public class TestUtility {
    static Map<String, String> systemProperties = System.getenv();
    static Properties properties = new Properties();

    private static String getProperty(String propertyName) {
        if(properties.size() == 0) {
            return systemProperties.get(propertyName);
        }
        else {
            return properties.getProperty(propertyName);
        }
    }

    public static void checkUrl(String url) {
        assertTrue("Check If URL is Proper", url.matches("^(((http|https)://)|mailto:).*$"));
    }

    public static Application.Helper getHelper() {
        try {
            if(systemProperties.containsKey("PROP_FILE")) {
                properties.load(new FileInputStream(systemProperties.get("PROP_FILE")));
            }

            CloudantClient cloudantClient = ClientBuilder
                    .url(new URL(getProperty("cloudant.url")))
                    .username(getProperty("cloudant.username"))
                    .password(getProperty("cloudant.password"))
                    .build();
            WolframRepository wolframRepository = new WolframRepository(getProperty("wolfram.apiKey"));
            return new Application.Helper(
                    cloudantClient,
                    wolframRepository,
                    getProperty("cloudant.chatDB"),
                    getProperty("cloudant.feedbackDB"),
                    getProperty("cloudant.explorerDB"),
                    getProperty("tmdb.apiKey")
            );
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
}
