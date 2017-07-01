package chatbot.lib.api.qa;

import chatbot.cache.WolframRepository;

/**
 * Created by ramgathreya on 7/1/17.
 */
public class WolframAlpha {
    private WolframRepository wolframRepository;

    public WolframAlpha(WolframRepository wolframRepository) {
        this.wolframRepository = wolframRepository;
    }

    public QAService.Data search(String question) {
        return wolframRepository.getAnswer(question).getResult();
    }

}
