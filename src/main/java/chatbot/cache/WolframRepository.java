package chatbot.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * Created by ramgathreya on 7/1/17.
 */
@Component
public class WolframRepository {
    private static final Logger logger = LoggerFactory.getLogger(WolframRepository.class);
    private String apiKey;

    @Autowired
    public WolframRepository(@Value("${wolfram.apiKey}") String apiKey) {
        this.apiKey = apiKey;
    }

    @Cacheable(value="wolfram", key="#question")
    public WolframModel getAnswer(String question) {
        logger.info("API KEY IS : " + apiKey);
        return new WolframModel(question).getAnswer(apiKey);
    }
}
