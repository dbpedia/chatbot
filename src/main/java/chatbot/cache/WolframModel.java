package chatbot.cache;

import chatbot.lib.api.dbpedia.SpotlightService;
import chatbot.lib.api.qa.QAService;
import com.wolfram.alpha.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ramgathreya on 7/1/17.
 */
public class WolframModel {
    private static final Logger logger = LoggerFactory.getLogger(WolframModel.class);

    private String question;
    private List<QAService.Data> answer = null;

    public String getQuestion() {
        return question;
    }

    public WolframModel setQuestion(String question) {
        this.question = question;
        return this;
    }

    public List<QAService.Data> getAnswer() {
        return answer;
    }

    public WolframModel setAnswer(List<QAService.Data> answer) {
        this.answer = answer;
        return this;
    }

    public WolframModel(String question) {
        this.question = question;
    }


    private String queryWolframAlpha(String apiKey) {
        WAEngine engine = new WAEngine();
        engine.setAppID(apiKey);
        engine.addFormat("plaintext");

        WAQuery query = engine.createQuery();
        query.setInput(question);
        String result = null;

        try {
            // For educational purposes, print out the URL we are about to send:
            System.out.println("Query URL:");
            System.out.println(engine.toURL(query));
            System.out.println("");

            // This sends the URL to the Wolfram|Alpha server, gets the XML result
            // and parses it into an object hierarchy held by the WAQueryResult object.
            WAQueryResult queryResult = engine.performQuery(query);

            if (queryResult.isError()) {
                return null;
            } else if (!queryResult.isSuccess()) {
                return null;
            } else {
                for (WAPod pod : queryResult.getPods()) {
                    if (!pod.isError()) {
                        if(pod.getTitle().equals("Result")) {
                            for (WASubpod subpod : pod.getSubpods()) {
                                for (Object element : subpod.getContents()) {
                                    if (element instanceof WAPlainText) {
                                        return ((WAPlainText) element).getText();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (WAException e) {
            e.printStackTrace();
        }
        return null;
    }

    public WolframModel getAnswer(String apiKey) {
        String wolframAnswer = queryWolframAlpha(apiKey);
        System.out.println("RESULT COULD BE: " + wolframAnswer);

        if (wolframAnswer != null) {
            answer = new ArrayList();
            // Try to Resolve to DBpedia using Spotlight
            String uri = new SpotlightService().search(wolframAnswer);
            // If URI found
            if (uri != null) {
//                answer.add(new QAService.Data());
            }
            else {

            }
        }

        return this;
    }
}
