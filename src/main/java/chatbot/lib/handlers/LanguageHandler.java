package chatbot.lib.handlers;

import chatbot.lib.response.Response;
import chatbot.rivescript.RiveScriptBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;

/**
 * Created by ramgathreya on 6/13/17.
 */
public class LanguageHandler {
    private static final Logger logger = LoggerFactory.getLogger(LanguageHandler.class);

    private String userId;
    private String[] language;
    private RiveScriptBot riveScriptBot;

    private static final HashMap<String, String[]> LANGUAGES = new HashMap<String, String[]>(){{
        // Service Name, Endpoint URL, Mailing List
        put("", new String[]{"DBpedia", "http://dbpedia.org/page/DBpedia", "dbpedia-discussion@lists.sourceforge.net"});
    }};

    public LanguageHandler(String userId, String language, RiveScriptBot riveScriptBot) {
        this.userId = userId;
        this.language = new String[]{language};
        this.riveScriptBot = riveScriptBot;
    }

    public List<Response> handleLanguageAbout() {
        return null;
    }
}
