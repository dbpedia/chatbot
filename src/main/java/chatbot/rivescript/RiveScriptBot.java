package chatbot.rivescript;

import chatbot.lib.Utility;
import com.rivescript.RiveScript;

/**
 * Created by ramgathreya on 5/11/17.
 */
public class RiveScriptBot extends RiveScript {

    public RiveScriptBot() {
        super();
        this.loadDirectory("src/main/resources/rivescript");
        this.sortReplies();
    }

    public String[] answer(String userId, String message) {

        String reply = this.reply(userId, message);

        // Fallback for unknown / unclear queries
        if (reply == null
                || reply.trim().isEmpty()
                || reply.length() < 8
                || reply.equalsIgnoreCase("I see.")
                || reply.equalsIgnoreCase("Please go on.")
                || reply.equalsIgnoreCase("Ok.")
                || reply.contains("ERR")) {

            reply = "Sorry, I didn’t quite understand that.\n" +
                    "You can try asking:\n" +
                    "- What is DBpedia?\n" +
                    "- Who is Albert Einstein?\n" +
                    "- What is RDF?\n" +
                    "- Tell me about Wikipedia";
        }

        return Utility.split(reply);
    }
}
