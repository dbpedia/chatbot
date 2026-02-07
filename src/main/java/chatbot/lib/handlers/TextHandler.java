package chatbot.lib.handlers;

import chatbot.Application;
import chatbot.lib.Utility;
import chatbot.lib.Constants;
import chatbot.lib.handlers.dbpedia.LanguageHandler;
import chatbot.lib.handlers.dbpedia.StatusCheckHandler;
import chatbot.lib.request.Request;
import chatbot.lib.request.TemplateType;
import chatbot.lib.response.ResponseData;
import chatbot.lib.response.ResponseGenerator;
import chatbot.rivescript.RiveScriptReplyType;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.languagetool.rules.RuleMatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by ramgathreya on 5/22/17.
 */
public class TextHandler {
    private static final Logger logger = LoggerFactory.getLogger(TextHandler.class);

    private Request request;
    private String textMessage;
    private Application.Helper helper;
    private boolean fallbackTriggered = false;

    private String sanitizeText(String message) {
        String result = message;
        try {
            List<RuleMatch> matches = helper.getLanguageTool().check(message);
            for (RuleMatch match : matches) {
                String error = message.substring(match.getFromPos(), match.getToPos());
                List<String> replacements = match.getSuggestedReplacements();
                if (replacements.size() > 0) {
                    result = result.replace(error, match.getSuggestedReplacements().get(0));
                }
            }
        } catch (Exception e) {
            logger.error("Error sanitizing text: {}", message, e);
        }
        return result;
    }

    public TextHandler(Request request, String textMessage, Application.Helper helper) {
        this.request = request;
        this.helper = helper;
        this.textMessage = sanitizeText(textMessage);
    }

    public ResponseGenerator handleTextMessage() throws Exception {
        ResponseGenerator responseGenerator = new ResponseGenerator();
        String[] rivescriptReply = helper.getRiveScriptBot().answer(request.getUserId(), textMessage);

        for (String reply : rivescriptReply) {
            if (Utility.isJSONObject(reply) == true) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(reply);
                switch (rootNode.get("type").getTextValue()) {
                    case RiveScriptReplyType.TEMPLATE_SCENARIO:
                        responseGenerator = new TemplateHandler(request,
                                Utility.split(rootNode.get("name").getTextValue(), Utility.PARAMETER_SEPARATOR), helper)
                                .handleTemplateMessage();
                        break;
                    case RiveScriptReplyType.LANGUAGE_SCENARIO:
                        responseGenerator = new LanguageHandler(request, rootNode.get("name").getTextValue(), helper)
                                .handleLanguageAbout();
                        break;
                    case RiveScriptReplyType.STATUS_CHECK_SCENARIO:
                        responseGenerator = new StatusCheckHandler(request, rootNode.get("name").getTextValue(), helper)
                                .handleStatusCheck();
                        break;
                    case RiveScriptReplyType.LOCATION_SCENARIO:
                        responseGenerator = new LocationHandler(request, rootNode.get("query").getTextValue(), helper)
                                .getLocation();
                        break;
                    case RiveScriptReplyType.FALLBACK_SCENARIO:
                        // Eliza
                        if (textMessage.endsWith("!") || textMessage.endsWith(".")) {
                            fallbackTriggered = true;
                            responseGenerator
                                    .addTextResponse(new ResponseData(helper.getEliza().processInput(textMessage)));
                        } else {
                            textMessage = rootNode.get("query").getTextValue(); // Use processed text message
                            responseGenerator = new NLHandler(request, textMessage, helper).answer();
                            // Only trigger fallback if NLHandler returned no valid response
                            if (responseGenerator.getResponse().size() == 0) {
                                fallbackTriggered = true;
                            }
                        }
                        break;
                }
            } else {
                responseGenerator.addTextResponse(new ResponseData(reply));
            }
        }

        // Add guided fallback suggestions when we could not understand
        if (fallbackTriggered || responseGenerator.getResponse().size() == 0) {
            responseGenerator = appendFallbackSuggestions(responseGenerator, textMessage, fallbackTriggered);
        }

        // Fallback when everything else fails Eliza will answer
        if (responseGenerator.getResponse().size() == 0) {
            responseGenerator.addTextResponse(new ResponseData(helper.getEliza().processInput(textMessage)));
        }
        return responseGenerator;
    }

    private ResponseGenerator appendFallbackSuggestions(ResponseGenerator responseGenerator, String originalText,
            boolean elizaResponded) {
        // If Eliza already provided a meaningful reply, use a non-contradictory message
        if (elizaResponded) {
            responseGenerator.addTextResponse(new ResponseData("Here are some things I can help with:"));
        } else {
            // Extract keyword for contextual fallback message
            String keyword = extractKeyword(originalText);

            String fallbackPrompt;
            if (keyword != null && !keyword.isEmpty()) {
                fallbackPrompt = "I didn't quite understand, but you might be asking about '" + keyword
                        + "'. Try one of these suggestions:";
            } else {
                // Use default RiveScript fallback when no keyword could be extracted
                String[] fallbackResult = helper.getRiveScriptBot().answer(request.getUserId(),
                        RiveScriptReplyType.FALLBACK_TEXT);
                if (fallbackResult != null && fallbackResult.length > 0) {
                    fallbackPrompt = fallbackResult[0];
                } else {
                    fallbackPrompt = "Sorry, I don't understand.";
                }
            }
            responseGenerator.addTextResponse(new ResponseData(fallbackPrompt));
        }

        ResponseData smartReplies = new ResponseData();

        // Fixed DBpedia-focused suggestions (no mixed/contextual items)
        smartReplies.addSmartReply(new ResponseData.SmartReply("What is DBpedia?", TemplateType.DBPEDIA_ABOUT));
        smartReplies
                .addSmartReply(new ResponseData.SmartReply("How can I contribute?", TemplateType.DBPEDIA_CONTRIBUTE));
        smartReplies.addSmartReply(new ResponseData.SmartReply("Is DBpedia up?",
                TemplateType.CHECK_SERVICE + Utility.STRING_SEPARATOR + Constants.DBPEDIA_SERVICE));
        smartReplies
                .addSmartReply(new ResponseData.SmartReply("Show me DBpedia datasets", TemplateType.DBPEDIA_DATASET));
        smartReplies.addSmartReply(
                new ResponseData.SmartReply("How do I search with DBpedia Lookup?", TemplateType.DBPEDIA_LOOKUP));
        smartReplies.addSmartReply(
                new ResponseData.SmartReply("Tell me about DBpedia mappings", TemplateType.DBPEDIA_MAPPINGS));

        responseGenerator.addSmartReplyResponse(smartReplies);
        return responseGenerator;
    }

    private String extractKeyword(String text) {
        if (text == null || text.trim().length() == 0) {
            return null;
        }
        String cleaned = text.replaceAll("[^A-Za-z0-9 ]", " ").toLowerCase();
        String[] tokens = cleaned.split(" +");
        String[] stop = new String[] { "what", "who", "where", "why", "how", "is", "are", "the", "a", "an", "of", "in",
                "on", "for", "with", "to", "tell", "me", "about", "please" };
        java.util.Set<String> stopSet = new java.util.HashSet<>();
        for (String s : stop)
            stopSet.add(s);

        String best = null;
        for (String t : tokens) {
            if (t.length() < 3)
                continue;
            if (stopSet.contains(t))
                continue;
            if (best == null || t.length() > best.length()) {
                best = t;
            }
        }
        return best;
    }

    public String getTextMessage() {
        return textMessage;
    }
}
