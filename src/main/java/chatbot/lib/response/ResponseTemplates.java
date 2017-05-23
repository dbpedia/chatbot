package chatbot.lib.response;

/**
 * Created by ramgathreya on 5/23/17.
 */
public class ResponseTemplates {
    public static ResponseData[] getStarterTemplate() {
        return new ResponseData[]{
                new ResponseData("https://image.flaticon.com/icons/png/512/64/64572.png", "Who ?", "Who is Albert Einstein?\nTell me about Barack Obama"),
                new ResponseData("https://image.flaticon.com/icons/svg/25/25400.svg", "What ?", "What is a planet?\nWhat is GST?"),
                new ResponseData("https://upload.wikimedia.org/wikipedia/commons/thumb/5/50/Circle-icons-gps.svg/120px-Circle-icons-gps.svg.png", "Where ?", "Where is the Eiffel Tower?\nWhat is Germany's capital?")
        };
    }
}
