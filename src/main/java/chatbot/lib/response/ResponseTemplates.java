package chatbot.lib.response;

import java.util.ArrayList;

/**
 * Created by ramgathreya on 5/23/17.
 */
public class ResponseTemplates {
    public static ResponseData[] getStarterTemplate() {
        return new ResponseData[]{
                new ResponseData("/images/icon-user-64.png", "Who ?", "Albert Einstein\nTell me about Barack Obama"),
                new ResponseData("/images/icon-help-64.png", "What ?", "What is a planet?\nWhat is GST?"),
                new ResponseData("/images/icon-compass-64.png", "Where ?", "Where is the Eiffel Tower?\nWhere is Germany's capital?")
        };
    }

    public static ResponseData getAboutDBpediaTemplate() {
        return new ResponseData("You can find more information here:", new ArrayList<ResponseData.ButtonData>(){{
            add(new ResponseData.ButtonData("About DBpedia", ResponseType.BUTTON_LINK, "http://wiki.dbpedia.org/about"));
            add(new ResponseData.ButtonData("Getting Started", ResponseType.BUTTON_LINK, "http://wiki.dbpedia.org/get-involved"));
        }});
    }
}
