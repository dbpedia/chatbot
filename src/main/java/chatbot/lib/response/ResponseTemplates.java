package chatbot.lib.response;

/**
 * Created by ramgathreya on 5/23/17.
 */
public class ResponseTemplates {
    public static ResponseData[] getStarterTemplate() {
        return new ResponseData[]{
                new ResponseData("/images/icon-user-64.png", "Who ?", "Who is Albert Einstein?\nTell me about Barack Obama"),
                new ResponseData("/images/icon-help-64.png", "What ?", "What is a planet?\nWhat is GST?"),
                new ResponseData("/images/icon-compass-64.png", "Where ?", "Where is the Eiffel Tower?\nWhat is Germany's capital?")
        };
    }
}
