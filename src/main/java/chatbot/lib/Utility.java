package chatbot.lib;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Created by ramgathreya on 5/23/17.
 */
public class Utility {
    public static String generateImageUrl(String baseUrl, String imageUrl) {
        if(imageUrl.startsWith("http")) {
            return imageUrl;
        }
        else {
            return baseUrl + imageUrl;
        }
    }

    public static String[] split(String string) {
        if(string.contains("|")) {
            return string.split("\\|");
        }
        else {
            return new String[]{string};
        }
    }


    public static boolean isJSONObject(String string) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            mapper.readTree(string);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
