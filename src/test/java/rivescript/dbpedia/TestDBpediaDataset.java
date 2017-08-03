package rivescript.dbpedia;

import org.junit.Test;
import rivescript.RiveScriptBase;

import java.util.ArrayList;

/**
 * Created by ramgathreya on 7/12/17.
 */
public class TestDBpediaDataset extends RiveScriptBase {
    @Test
    public void testDBpediaDataset() {
        String[] testCases = new String[]{"I'm starting my master research work on NLP. I want to use DBpedia dataset. But I don't know how to use it. Who can help me?", "Download nlp dataset"};
        String[] expectedAnswer = new String[]{"{\"type\": \"template\", \"name\": \"dbpedia-dataset-nlp\"}"};
        checkAnswers(testCases, expectedAnswer, true);
    }
}
