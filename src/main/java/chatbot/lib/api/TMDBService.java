package chatbot.lib.api;

import chatbot.lib.Constants;
import chatbot.lib.Utility;
import chatbot.lib.api.dbpedia.LookupService;
import chatbot.lib.response.ResponseData;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ramgathreya on 7/3/17.
 */
public class TMDBService {
    private static final Logger logger = LoggerFactory.getLogger(TMDBService.class);

    private static final String URL = "https://api.themoviedb.org/3";
    private static final String CAST = "cast";
    private static final String CREW = "crew";

    private String apiKey;
    private HttpClient client;
    private SPARQL sparql;

    public TMDBService(String apiKey, SPARQL sparql) {
        this.apiKey = apiKey;
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(Constants.API_TIMEOUT).build();
        client = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
        this.sparql = sparql;
    }

    private JsonNode makeRequest(String url) {
        try {
            HttpGet httpGet = new HttpGet(url);
            httpGet.addHeader("Accept", "application/json");
            HttpResponse response = client.execute(httpGet);

            String entities = EntityUtils.toString(response.getEntity());
            return new ObjectMapper().readTree(entities);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getTvOrMovieId(String url, String query) {
        try {
            url += "?api_key=" + apiKey + "&query=" + Utility.urlEncode(query);
            JsonNode node = makeRequest(url);
            return node.get("results").get(0).get("id").getBigIntegerValue().toString();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getTvId(String tvSeriesName) {
        String url = URL + "/search/tv";
        return getTvOrMovieId(url, tvSeriesName);
    }

    public String getMovieId(String movieName) {
        String url = URL + "/search/movie";
        return getTvOrMovieId(url, movieName);
    }

    private ArrayList<ResponseData> getCastOrCrew(String url, String type) {
        ArrayList<ResponseData> datas = new ArrayList<>();
        try {
            JsonNode people = makeRequest(url).get(type);
            List<String> peopleNames = new ArrayList<>();

            for(JsonNode person : people) {
                peopleNames.add(person.get("name").getTextValue());
            }

            if (peopleNames.size() > ResponseData.MAX_DATA_SIZE) {
                peopleNames = peopleNames.subList(0, ResponseData.MAX_DATA_SIZE);
            }

            for (String personName : peopleNames) {
                String uri = new LookupService().search(personName);
                if(uri != null) {
                    datas.add(sparql.getEntityInformation(uri));
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return datas;
    }

    public ArrayList<ResponseData> getTvCast(String tvId) {
        String url = URL + "/tv/" + tvId + "/credits?api_key=" + apiKey;
        return getCastOrCrew(url, CAST);
    }

    public ArrayList<ResponseData> getTvCrew(String tvId) {
        String url = URL + "/tv/" + tvId + "/credits?api_key=" + apiKey;
        return getCastOrCrew(url, CREW);
    }

    public ArrayList<ResponseData> getMovieCast(String movieId) {
        String url = URL + "/movie/" + movieId + "/credits?api_key=" + apiKey;
        return getCastOrCrew(url, CAST);
    }

    public ArrayList<ResponseData> getMovieCrew(String movieId) {
        String url = URL + "/movie/" + movieId + "/credits?api_key=" + apiKey;
        return getCastOrCrew(url, CREW);
    }
}
