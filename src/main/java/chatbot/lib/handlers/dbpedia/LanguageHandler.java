package chatbot.lib.handlers.dbpedia;

import chatbot.lib.request.Request;
import chatbot.lib.response.Response;
import chatbot.lib.response.ResponseData;
import chatbot.lib.response.ResponseGenerator;
import chatbot.lib.response.ResponseType;
import chatbot.rivescript.RiveScriptBot;
import chatbot.rivescript.RiveScriptReplyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ramgathreya on 6/13/17.
 */
public class LanguageHandler {
    private static final Logger logger = LoggerFactory.getLogger(LanguageHandler.class);

    private Request request;
    private String language;
    private RiveScriptBot riveScriptBot;

    // At the moment DBpedia has around 15 language chapters, that are concerned with improving the extraction of data from language-specific Wikipedia versions. For example you can look at the Spanish DBpedia or the Dutch DBpedia.

    private static final HashMap<String, String[]> LANGUAGES = new HashMap<String, String[]>(){{
        put("arabic", new String[]{"DBpedia Arabic", "http://ar.dbpedia.org", "http://ar.dbpedia.org/sparql"});
        put("basque", new String[]{"DBpedia Basque", "http://eu.dbpedia.org", "http://eu.dbpedia.org/sparql"});
        put("czech", new String[]{"DBpedia Czech", "http://cs.dbpedia.org/wiki", "http://cs.dbpedia.org/sparql"});
        put("dutch", new String[]{"DBpedia Dutch", "http://nl.dbpedia.org/web", "http://nl.dbpedia.org/sparql"});
        put("english", new String[]{"DBpedia English", "http://dbpedia.org", "http://dbpedia.org/sparql"});
        put("esperanto", new String[]{"DBpedia Esperanto", "http://eo.dbpedia.org", "http://eo.dbpedia.org/sparql"});
        put("french", new String[]{"DBpedia French", "http://fr.dbpedia.org", "http://fr.dbpedia.org/sparql"});
        put("greek", new String[]{"DBpedia Greek", "http://el.dbpedia.org", "http://el.dbpedia.org/sparql"});
        put("german", new String[]{"DBpedia German", "http://de.dbpedia.org", "http://de.dbpedia.org/sparql"});
        put("indonesian", new String[]{"DBpedia Indonesian", "http://id.dbpedia.org", "http://id.dbpedia.org/sparql"});
        put("italian", new String[]{"DBpedia Italian", "http://it.dbpedia.org/?lang=en", "http://it.dbpedia.org/sparql"});
        put("japanese", new String[]{"DBpedia Japanese", "http://ja.dbpedia.org", "http://ja.dbpedia.org/sparql"});
        put("korean", new String[]{"DBpedia Korean", "http://ko.dbpedia.org", "http://ko.dbpedia.org/sparql"});
        put("polish", new String[]{"DBpedia Polish", "http://pl.dbpedia.org", "http://pl.dbpedia.org/sparql"});
        put("portuguese", new String[]{"DBpedia Portuguese", "http://pt.dbpedia.org", "http://pt.dbpedia.org/sparql"});
        put("russian", new String[]{"DBpedia Russian", "http://ru.dbpedia.org", "http://ru.dbpedia.org/sparql"});
        put("spanish", new String[]{"DBpedia Spanish", "http://es.dbpedia.org", "http://es.dbpedia.org/sparql"});
        put("swedish", new String[]{"DBpedia Swedish", "http://sv.dbpedia.org", "http://sv.dbpedia.org/sparql"});
        put("ukrainian", new String[]{"DBpedia Ukrainian", "http://uk.dbpedia.org", "http://uk.dbpedia.org/sparql"});
    }};

    public LanguageHandler(Request request, String language, RiveScriptBot riveScriptBot) {
        this.request = request;
        this.language = language;
        this.riveScriptBot = riveScriptBot;
    }

    public ResponseGenerator handleLanguageAbout() {
        ResponseGenerator responseGenerator = new ResponseGenerator();

        if(!language.equals("")) {
            String[] lang = LANGUAGES.get(language);
            responseGenerator.addButtonTextResponse(new ResponseData(
                riveScriptBot.answer(request.getUserId(), RiveScriptReplyType.DBPEDIA_LANGUAGE_TEXT + " " + lang[0])[0],
                new ArrayList<ResponseData.Button>(){{
                    add(new ResponseData.Button("Official Website", ResponseType.BUTTON_LINK, lang[1]));
                    add(new ResponseData.Button("SPARQL Endpoint", ResponseType.BUTTON_LINK, lang[2]));
                }}
            ));
        }
        else {
            responseGenerator.addButtonTextResponse(new ResponseData(
                    MessageFormat.format("At the moment DBpedia has {0} language chapters that are concerned with improving the extraction of data from language-specific Wikipedia versions.", LANGUAGES.size()),
                    new ArrayList<ResponseData.Button>(){{
                        add(new ResponseData.Button("All Chapters", ResponseType.BUTTON_LINK, "http://wiki.dbpedia.org/about/language-chapters"));
                    }}
            ));
        }
        return responseGenerator;
    }
}
