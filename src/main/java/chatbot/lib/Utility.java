package chatbot.lib;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.Locale;

/**
 * Created by ramgathreya on 5/23/17.
 */
public class Utility {
    // Used by RiveScript when it needs to send back multiple parameters as payload to parameter handler
    public static final String PARAMETER_SEPARATOR = "|";
    public static final String STRING_SEPARATOR = "__";

    public static String generateImageUrl(String baseUrl, String imageUrl) {
        if(imageUrl.startsWith("http")) {
            return imageUrl;
        }
        else {
            return baseUrl + imageUrl;
        }
    }

    public static String[] split(String string, String separator) {
        if(string.contains(separator)) {
            return string.split("\\" + separator);
        }
        else {
            return new String[]{string};
        }
    }

    public static String[] split(String string) {
        return split(string, STRING_SEPARATOR);
    }

    public static boolean isJSONObject(String string) {
        try {
            new ObjectMapper().readTree(string);
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

    public static <T> String toJson(T object) {
        try {
            org.codehaus.jackson.map.ObjectMapper mapper = new org.codehaus.jackson.map.ObjectMapper();
            return mapper.writeValueAsString(object);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T toObject(String json, Class T) {
        try {
            org.codehaus.jackson.map.ObjectMapper mapper = new org.codehaus.jackson.map.ObjectMapper();
            return (T) mapper.readValue(json, T);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String urlEncode(String text) throws UnsupportedEncodingException {
        try {
            return URLEncoder.encode(text, "UTF-8");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String capitalizeAll(String text) {
        String c = (text != null)? text.trim() : "";
        String[] words = c.split(" ");
        String result = "";
        for(String w : words){
            result += (w.length() > 1? w.substring(0, 1).toUpperCase(Locale.US) + w.substring(1, w.length()).toLowerCase(Locale.US) : w) + " ";
        }
        return result.trim();
    }

    public static String convertDBpediaToWikipediaURL(String url) {
        String[] urlsParts = url.split("/");
        return "https://en.wikipedia.org/wiki/" + urlsParts[urlsParts.length - 1];
    }
}
