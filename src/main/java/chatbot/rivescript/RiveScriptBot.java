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
        return Utility.split(this.reply(userId, message));
    }
}
