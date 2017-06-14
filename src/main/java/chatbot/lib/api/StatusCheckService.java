package chatbot.lib.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by ramgathreya on 6/12/17.
 */

// Checks if specific service is working
public class StatusCheckService {
    private static final Logger logger = LoggerFactory.getLogger(StatusCheckService.class);

    private static final int TIMEOUT = 50000;

    private String url;

    public String getUrl() {
        return url;
    }

    public StatusCheckService setUrl(String url) {
        this.url = url;
        return this;
    }

    public boolean isOnline() {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url)
                    .openConnection();
            connection.setConnectTimeout(TIMEOUT);
            connection.setReadTimeout(TIMEOUT);
            connection.setRequestMethod("HEAD");
            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                return false;
            }
        } catch (IOException exception) {
            return false;
        }
        return true;
    }
}
