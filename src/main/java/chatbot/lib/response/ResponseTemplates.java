package chatbot.lib.response;

import java.util.ArrayList;

/**
 * Created by ramgathreya on 5/23/17.
 */
public class ResponseTemplates {
    public static ResponseData[] getHelperTemplate() {
        return new ResponseData[]{
                new ResponseData("/images/icon-dbpedia-92.png", "About DBpedia", "What is DBpedia?\nHow can I contribute to DBpedia?"),
                new ResponseData("/images/icon-user-92.png", "Who ?", "Albert Einstein\nTell me about Barack Obama"),
                new ResponseData("/images/icon-help-92.png", "What ?", "What is a planet?\nWhat is Mathematics?"),
                new ResponseData("/images/icon-compass-92.png", "Where ?", "Where is the Eiffel Tower?\nWhere is Germany's capital?")
        };
    }

    public static ResponseData getAboutDBpediaTemplate() {
        return new ResponseData("You can find more information here:", new ArrayList<ResponseData.Button>(){{
            add(new ResponseData.Button("About DBpedia", ResponseType.BUTTON_LINK, "http://wiki.dbpedia.org/about"));
            add(new ResponseData.Button("Getting Started", ResponseType.BUTTON_LINK, "http://wiki.dbpedia.org/get-involved"));
            add(new ResponseData.Button("Tutorial", ResponseType.BUTTON_LINK, "http://semanticweb.org/wiki/Getting_data_from_the_Semantic_Web.html"));
        }});
    }

    public static ResponseData getContributeTemplate() {
        return new ResponseData("You can:\n1 - Look at open issues if you want to contribute to the codebase\n2 - Improve Documentation\n3 - Join the discussion on upcoming features, releases and issues", new ArrayList<ResponseData.Button>(){{
            add(new ResponseData.Button("Get Involved", ResponseType.BUTTON_LINK, "http://wiki.dbpedia.org/get-involved"));
            add(new ResponseData.Button("Mailing List", ResponseType.BUTTON_LINK, "https://lists.sourceforge.net/lists/listinfo/dbpedia-discussion"));
            add(new ResponseData.Button("Slack", ResponseType.BUTTON_LINK, "https://dbpedia.slack.com/"));
        }});
    }
}
