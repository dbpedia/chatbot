package chatbot.lib.api;

import chatbot.lib.Constants;
import chatbot.lib.Utility;
import chatbot.lib.response.Response;
import chatbot.lib.response.ResponseData;
import chatbot.lib.response.ResponseType;
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
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by ramgathreya on 7/27/17.
 */
public class NominatimService {
    private static final Logger logger = LoggerFactory.getLogger(NominatimService.class);
    private static final String URL = "http://nominatim.openstreetmap.org";

    private int maxHits = 1;
    private HttpClient client;

    public NominatimService() {
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(Constants.API_TIMEOUT).build();
        client = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
    }

    public ResponseData reverseGeoCode(String query) {
        try {
            String url = "/search?q=" + Utility.urlEncode(query) + "&addressdetails=1&extratags=1&limit=" + maxHits;
            HttpGet httpGet = new HttpGet(URL + url + "&format=json");
            httpGet.addHeader("Accept", "application/json");
            HttpResponse response = client.execute(httpGet);

            // Error Scenario
            if(response.getStatusLine().getStatusCode() >= 400) {
                return null;
            }
            else {
                String responseString = EntityUtils.toString(response.getEntity());
                JsonNode responseObj = new ObjectMapper().readTree(responseString).get(0);
                JsonNode extratags = responseObj.get("extratags");
                JsonNode address = responseObj.get("address");
                ResponseData place = new ResponseData();

                place.setTitle(query + " (Location Info)");
                place.setText(responseObj.get("display_name").getTextValue());
                if(extratags != null) {
                    if(extratags.get("image") != null) {
                        place.setImage(extratags.get("image").getTextValue());
                    }
                    if(extratags.get("website") != null) {
                        String website = extratags.get("website").getTextValue();
                        place.addField(new ResponseData.Field()
                                .setName("Website")
                                .setValues(new LinkedHashMap<String, String>(){{
                                    put(website, website);
                                }})
                        );
                    }
                    if(extratags.get("opening_hours") != null) {
                        place.addField(new ResponseData.Field("Opening Hours", extratags.get("opening_hours").getTextValue()));
                    }
                }

                if(address != null) {
                    if(address.get("country") != null) {
                        place.addField(new ResponseData.Field("Country", address.get("country").getTextValue()));
                    }
                    if(address.get("state") != null) {
                        place.addField(new ResponseData.Field("State", address.get("state").getTextValue()));
                    }
                    if(address.get("city") != null) {
                        place.addField(new ResponseData.Field("City", address.get("city").getTextValue()));
                    }
                }

                place.addField(new ResponseData.Field("Latitude", responseObj.get("lat").getTextValue()));
                place.addField(new ResponseData.Field("Longitude", responseObj.get("lon").getTextValue()));
                place.addButton(new ResponseData.Button("View Map", ResponseType.BUTTON_LINK, URL + url));
                return place;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getMaxHits() {
        return maxHits;
    }

    public NominatimService setMaxHits(int maxHits) {
        this.maxHits = maxHits;
        return this;
    }
}
