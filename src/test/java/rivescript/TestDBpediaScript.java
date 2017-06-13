package rivescript;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by ramgathreya on 6/12/17.
 */
public class TestDBpediaScript extends TestRiveScriptBase {

    @Test
    public void testDBpedia() {
        String[] testCases = new String[]{"dbpedia", "What is DBpedia", "Can you tell me about DBpedia", "Getting started with DBpedia", "Idiots guide to dbpedia", "dbpedia tutorial", "dbpedia tutorials", "can you give me tutorials on DBpedia"};
        String[] expectedAnswer = new String[]{"{\"type\": \"template\", \"name\": \"dbpedia-about\"}"};
        checkAnswers(testCases, expectedAnswer, true);
    }

    @Test
    public void testDBpediaServiceCheck() {
        String[] testCases = new String[]{"dbpedia status", "dbpedia down", "dbpedia down right now", "DBPedia down?", "DbPedia is down", "DBpedia down?", "is dbpedia down right now", "check if dbpedia is down", "check if dbpedia is down right now", "How long is DBpedia going to be down ???"};
        String[] expectedAnswer = new String[]{"{\"type\": \"status_check\", \"name\": \"dbpedia\"}"};
        checkAnswers(testCases, expectedAnswer, true);
    }

    @Test
    public void testDBpediaContribute() {
        String[] testCases = new String[]{"how to contribute to dbpedia", "how to contribute to dbpedia project", "contribute to dbpedia project", "how can i contribute to dbpedia???"};
        String[] expectedAnswer = new String[]{"{\"type\": \"template\", \"name\": \"dbpedia-contribute\"}"};
        checkAnswers(testCases, expectedAnswer, true);
    }

//    @Test
//    public void testDBpediaLanguages() {
//        String[] testCases = new String[]{"Indonesian DBpedia", "arabic dbpedia", "DBPedia Arabic Support", "dbpedia in greek"};
//        String[] expectedAnswer = new String[]{"LANGUAGE SPECIFIC DBPEDIA"};
//        checkAnswers(testCases, expectedAnswer, true);
//    }
}
