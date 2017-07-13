package chatbot.lib.handlers.templates.dbpedia;

import chatbot.Application;
import chatbot.lib.handlers.TemplateHandler;
import chatbot.lib.request.Request;
import chatbot.lib.request.TemplateType;
import chatbot.lib.response.ResponseData;
import chatbot.lib.response.ResponseGenerator;
import chatbot.lib.response.ResponseType;

import java.util.ArrayList;

/**
 * Created by ramgathreya on 7/12/17.
 */
public class LookupTemplateHandler extends TemplateHandler{
    public LookupTemplateHandler(Request request, String[] payload, Application.Helper helper) {
        super(request, payload, helper);
    }

    public ResponseGenerator handleTemplateMessage() {
        ResponseGenerator responseGenerator = new ResponseGenerator();
        switch (payload[0]) {
            case TemplateType.DBPEDIA_LOOKUP:
                responseGenerator.addTextResponse(new ResponseData("DBpedia Lookup is a web service that can be used to look up DBpedia URIs by related keywords. Two APIs are offered: Keyword Search and Prefix Search. A hosted version of the Lookup service is available on the DBpedia server infrastructure."));
                responseGenerator.addButtonTextResponse(new ResponseData("You can find the official documentation here:", new ArrayList<ResponseData.Button>(){{
                    add(new ResponseData.Button("Documentation", ResponseType.BUTTON_LINK, "https://github.com/dbpedia/lookup"));
                }}));
                responseGenerator.addButtonTextResponse(new ResponseData("Would you like to know about:", new ArrayList<ResponseData.Button>(){{
                    add(new ResponseData.Button("Keyword Search", ResponseType.BUTTON_PARAM, TemplateType.DBPEDIA_LOOKUP_KEYWORD_SEARCH));
                    add(new ResponseData.Button("Prefix Search", ResponseType.BUTTON_PARAM, TemplateType.DBPEDIA_LOOKUP_PREFIX_SEARCH));
                    add(new ResponseData.Button("Lookup Parameters", ResponseType.BUTTON_PARAM, TemplateType.DBPEDIA_LOOKUP_PARAMETERS));
                }}));
                break;
            case TemplateType.DBPEDIA_LOOKUP_KEYWORD_SEARCH:
                responseGenerator.addTextResponse(new ResponseData("The DBpedia Lookup Keyword Search API can be used to find related DBpedia resources for a given string. The string may consist of a single or multiple words."));
                responseGenerator.addButtonTextResponse(new ResponseData("Example: Places that have the keyword 'berlin'", new ArrayList<ResponseData.Button>(){{
                    add(new ResponseData.Button("View Result", ResponseType.BUTTON_LINK, "http://lookup.dbpedia.org/api/search/KeywordSearch?QueryClass=place&QueryString=berlin"));
                }}));
                responseGenerator.addButtonTextResponse(new ResponseData("Would you like to know about:", new ArrayList<ResponseData.Button>(){{
                    add(new ResponseData.Button("Prefix Search", ResponseType.BUTTON_PARAM, TemplateType.DBPEDIA_LOOKUP_PREFIX_SEARCH));
                    add(new ResponseData.Button("Lookup Parameters", ResponseType.BUTTON_PARAM, TemplateType.DBPEDIA_LOOKUP_PARAMETERS));
                    add(new ResponseData.Button("DBpedia Lookup", ResponseType.BUTTON_PARAM, TemplateType.DBPEDIA_LOOKUP));
                }}));
                break;
            case TemplateType.DBPEDIA_LOOKUP_PREFIX_SEARCH:
                responseGenerator.addTextResponse(new ResponseData("The DBpedia Lookup Prefix Search API can be used to implement autocomplete input boxes. For a given partial keyword like berl the API returns URIs of related DBpedia resources like http://dbpedia.org/resource/Berlin."));
                responseGenerator.addButtonTextResponse(new ResponseData("Example: Top five resources for which a keyword starts with 'berl'", new ArrayList<ResponseData.Button>(){{
                    add(new ResponseData.Button("View Result", ResponseType.BUTTON_LINK, "http://lookup.dbpedia.org/api/search/PrefixSearch?QueryClass=&MaxHits=5&QueryString=berl"));
                }}));
                responseGenerator.addButtonTextResponse(new ResponseData("Would you like to know about:", new ArrayList<ResponseData.Button>(){{
                    add(new ResponseData.Button("Keyword Search", ResponseType.BUTTON_PARAM, TemplateType.DBPEDIA_LOOKUP_KEYWORD_SEARCH));
                    add(new ResponseData.Button("Lookup Parameters", ResponseType.BUTTON_PARAM, TemplateType.DBPEDIA_LOOKUP_PARAMETERS));
                    add(new ResponseData.Button("DBpedia Lookup", ResponseType.BUTTON_PARAM, TemplateType.DBPEDIA_LOOKUP));
                }}));
                break;
            case TemplateType.DBPEDIA_LOOKUP_PARAMETERS:
                responseGenerator.addTextResponse(new ResponseData("The query parameters accepted by DBpedia Lookup are:\n1 - QueryString: a string for which a DBpedia URI should be found.\n2 - QueryClass: a DBpedia class from the Ontology that the results should have (for owl#Thing and untyped resource, leave this parameter empty).\n3 - MaxHits: the maximum number of returned results (default: 5)\n"));
                responseGenerator.addButtonTextResponse(new ResponseData("Would you like to know about:", new ArrayList<ResponseData.Button>(){{
                    add(new ResponseData.Button("Keyword Search", ResponseType.BUTTON_PARAM, TemplateType.DBPEDIA_LOOKUP_KEYWORD_SEARCH));
                    add(new ResponseData.Button("Prefix Search", ResponseType.BUTTON_PARAM, TemplateType.DBPEDIA_LOOKUP_PREFIX_SEARCH));
                    add(new ResponseData.Button("DBpedia Lookup", ResponseType.BUTTON_PARAM, TemplateType.DBPEDIA_LOOKUP));
                }}));
                break;
        }
        return responseGenerator;
    }
}
