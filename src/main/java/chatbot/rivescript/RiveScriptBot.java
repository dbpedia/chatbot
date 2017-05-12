package chatbot.rivescript;

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
}
