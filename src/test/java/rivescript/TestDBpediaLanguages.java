package rivescript;

import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by ramgathreya on 6/20/17.
 */
public class TestDBpediaLanguages extends TestRiveScriptBase {

    @Test
    public void testDBpediaLanguages() {
        String[] testCases = new String[]{"Indonesian DBpedia", "arabic dbpedia", "DBPedia Arabic Support", "dbpedia in greek", "arabic chapter", "dbpedia arabic chapter", "dbpedia in other languages"};
        ArrayList<String[]> expectedAnswer = new ArrayList<String[]>(){{
            add(new String[]{"{\"type\": \"language\", \"name\": \"indonesian\"}"});
            add(new String[]{"{\"type\": \"language\", \"name\": \"arabic\"}"});
            add(new String[]{"{\"type\": \"language\", \"name\": \"arabic\"}"});
            add(new String[]{"{\"type\": \"language\", \"name\": \"greek\"}"});
            add(new String[]{"{\"type\": \"language\", \"name\": \"arabic\"}"});
            add(new String[]{"{\"type\": \"language\", \"name\": \"arabic\"}"});
            add(new String[]{"{\"type\": \"language\", \"name\": \"\"}"});
        }};
        checkAnswers(testCases, expectedAnswer, true);
    }
}
