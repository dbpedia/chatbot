package chatbot.lib.handlers.templates;

import chatbot.Application;
import chatbot.lib.Ontology;
import chatbot.lib.Utility;
import chatbot.lib.api.SPARQL;
import chatbot.lib.api.TMDBService;
import chatbot.lib.api.dbpedia.GenesisService;
import chatbot.lib.handlers.TemplateHandler;
import chatbot.lib.handlers.templates.entity.MovieTemplateHandler;
import chatbot.lib.handlers.templates.entity.TVTemplateHandler;
import chatbot.lib.request.Request;
import chatbot.lib.request.TemplateType;
import chatbot.lib.response.ResponseData;
import chatbot.lib.response.ResponseGenerator;
import chatbot.lib.response.ResponseType;
import chatbot.rivescript.RiveScriptReplyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * Created by ramgathreya on 7/3/17.
 */
public class OptionsTemplateHandler extends TemplateHandler {
    private static final Logger logger = LoggerFactory.getLogger(OptionsTemplateHandler.class);

    public OptionsTemplateHandler(Request request, String[] payload, Application.Helper helper) {
        super(request, payload, helper);
    }

    protected ResponseGenerator getSimilarOrRelatedEntities(String scenario, String uri) {
        ResponseGenerator responseGenerator = new ResponseGenerator();
        String uris = null;

        switch (scenario) {
            case TemplateType.LOAD_SIMILAR:
                uris = new GenesisService().getSimilarEntities(uri);
                break;
            case TemplateType.LOAD_RELATED:
                uris = new GenesisService().getRelatedEntities(uri);
                break;
        }
        // Set Fallback when GENESIS returns null or invalid response
        if(uris == null) {
            return responseGenerator.setNoResultsResponse(request, helper.getRiveScriptBot());
        }
        return responseGenerator.addCarouselResponse(helper.getSparql().getEntitiesByURIs(uris));
    }

    protected ResponseGenerator getTMDBData() {
        ResponseGenerator responseGenerator = new ResponseGenerator();
        ArrayList<ResponseData> results = new ArrayList<>();
        switch(payload[0]) {
            case TemplateType.TV_CAST:
                results = new TMDBService(helper.getTmdbApiKey(), helper.getSparql()).getTvCast(payload[2]);
                break;
            case TemplateType.TV_CREW:
                results = new TMDBService(helper.getTmdbApiKey(), helper.getSparql()).getTvCrew(payload[2]);
                break;
            case TemplateType.MOVIE_CAST:
                results = new TMDBService(helper.getTmdbApiKey(), helper.getSparql()).getMovieCast(payload[2]);
                break;
            case TemplateType.MOVIE_CREW:
                results = new TMDBService(helper.getTmdbApiKey(), helper.getSparql()).getMovieCrew(payload[2]);
                break;
        }

        if(results.size() == 0) {
            responseGenerator.setNoResultsResponse(request, helper.getRiveScriptBot());
        }
        else {
            responseGenerator.addCarouselResponse(results);
        }
        return responseGenerator;
    }

    private ResponseData getDefaultOptions(String uri, String label) {
        return new ResponseData(helper.getRiveScriptBot().answer(request.getUserId(), RiveScriptReplyType.LEARN_MORE_TEXT + " " + label)[0])
                .addSmartReply(new ResponseData.SmartReply("Similar", TemplateType.LOAD_SIMILAR + Utility.STRING_SEPARATOR + uri + Utility.STRING_SEPARATOR + label))
                .addSmartReply(new ResponseData.SmartReply("Related", TemplateType.LOAD_RELATED + Utility.STRING_SEPARATOR + uri + Utility.STRING_SEPARATOR + label));
    }

    private ResponseGenerator getLearnMoreOptions(String uri, String label) {
        ResponseGenerator responseGenerator = new ResponseGenerator();
        try {
            String types = helper.getSparql().getRDFTypes(uri);

            if (types.contains(Ontology.DBO_TELEVISION_SHOW)) {
                TMDBService tmdbService = new TMDBService(helper.getTmdbApiKey(), helper.getSparql());
                String tvId = tmdbService.getTvId(label);
                if (tvId != null) {
                    return responseGenerator.addSmartReplyResponse(new TVTemplateHandler(request, payload, helper).getTVOptions(uri, tvId, label));
                }
            }
            if (types.contains(Ontology.DBO_FILM)) {
                TMDBService tmdbService = new TMDBService(helper.getTmdbApiKey(), helper.getSparql());
                String movieId = tmdbService.getMovieId(label);
                if (movieId != null) {
                    return responseGenerator.addSmartReplyResponse(new MovieTemplateHandler(request, payload, helper).getMovieOptions(uri, movieId, label));
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        return responseGenerator.addSmartReplyResponse(getDefaultOptions(uri, label));
    }

    public ResponseGenerator handleTemplateMessage() {
        ResponseGenerator responseGenerator = new ResponseGenerator();
        switch (payload[0]) {
            // Pagination
            case TemplateType.LOAD_MORE:
                SPARQL.ResponseInfo responseInfo = Utility.toObject(payload[1], SPARQL.ResponseInfo.class);

                responseGenerator.addCarouselResponse(responseInfo.nextPage(helper.getSparql()));
                // Pagination
                if (responseInfo.hasMorePages()) {
                    responseInfo.next();
                    responseGenerator.addButtonTextResponse(new ResponseData(helper.getRiveScriptBot().answer(request.getUserId(), RiveScriptReplyType.LOAD_MORE_TEXT)[0], new ArrayList<ResponseData.Button>(){{
                        add(new ResponseData.Button("Load More", ResponseType.BUTTON_PARAM, TemplateType.LOAD_MORE + Utility.STRING_SEPARATOR + Utility.toJson(responseInfo)));
                    }}));
                }
                break;
            case TemplateType.LOAD_SIMILAR:
            case TemplateType.LOAD_RELATED:
                responseGenerator = getSimilarOrRelatedEntities(payload[0], payload[1]);
                responseGenerator.addSmartReplyResponse(getDefaultOptions(payload[1], payload[2]));
                break;
            // More options for a specific entity
            case TemplateType.LEARN_MORE:
                responseGenerator = getLearnMoreOptions(payload[1], payload[2]);
                break;
        }
        return responseGenerator;
    }
}
