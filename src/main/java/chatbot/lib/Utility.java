package chatbot.lib;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.text.DecimalFormat;

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

    public static boolean isInteger(String s) {
        return s.matches("\\d+");
    }

    public static String formatInteger(String s) {
        int amount = Integer.parseInt(s);
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(amount);
    }
}
