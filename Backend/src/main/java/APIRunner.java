import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import io.javalin.plugin.bundled.CorsPluginConfig;
import kong.unirest.Unirest;

public class APIRunner {

    private Gson gson = new Gson();
    private String token;
    private String searchKey;
    private String clientId, clientSecret;

    public APIRunner() {
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    }

    public static void main(String[] args) {
        APIRunner runner = new APIRunner();
        Javalin app = Javalin.create(javalinConfig -> javalinConfig.plugins.enableCors(cors -> cors.add(CorsPluginConfig::anyHost))).start(8080);

        app.get("search/{key}", ctx -> {
            runner.searchNasa(ctx);
        });

        app.get("track/{track}", ctx -> {
            runner.searchSpotify(ctx);
        });

        app.get("play", ctx -> {
            runner.playTrack(ctx);
        });

        app.get("token", ctx -> {
            runner.spot(ctx);
        });
    }

    public void spot(Context ctx) {
        clientId = "38d9e5c35e734857b7e0f633c1fafd99";
        clientSecret = "c3b46ed9a4f04ea2951bbdc0ed54b6f7";
        String url = "https://accounts.spotify.com/api/token";

        Map result = Unirest.post(url)
                .basicAuth(clientId,clientSecret)
                .field("grant_type","client_credentials")
                .asObject(i -> new Gson().fromJson(i.getContentReader(), HashMap.class))
                .getBody();
        ctx.json(result);
        Unirest.shutDown();
    }


    public void searchSpotify(Context ctx) { ///////////
        String url = "https://api.spotify.com/v1/search";

        if (ctx.req().getHeader("Authorization") == null) {
            token = "";
        } else {
            token = ctx.req().getHeader("Authorization");
        }

        try {
            Map result = Unirest.get(url)
                    .queryString("q", searchKey)
                    .queryString("type", "track")
                    .queryString("limit", 10)
                    .header("Authorization", ("Bearer " + token))
                    .header("Content-Type", "application/json")
                    .asObject(i -> new Gson().fromJson(i.getContentReader(), HashMap.class))
                    .getBody();
            ctx.json(result);
            Unirest.shutDown();

        } catch (Exception e) {
            ctx.status(500).result("An error occurred while calling the API");
        }
    }

    public void playTrack(Context ctx){
        System.out.println("here");
        String url = "https://api.spotify.com/v1/me/player/play";
        String uri = "spotify:track:6ozxplTAjWO0BlUxN8ia0A";

        if (ctx.req().getHeader("Authorization") == null) {
            token = "";
        } else {
            token = ctx.req().getHeader("Authorization");
        }

        System.out.println(token);

        Map result = Unirest.put(url)
                .header("Authorization", ("Bearer " + token))
                .header("Content-Type", "application/json")
                .asObject(i -> new Gson().fromJson(i.getContentReader(), HashMap.class))
                .getBody();
        ctx.json(result);
        Unirest.shutDown();
    }

    public void searchNasa(Context ctx) {
        searchKey = ctx.pathParam("key");

        try {
            URL url = new URL("https://images-api.nasa.gov/search?q=" + searchKey);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("GET");
            System.out.println(http.getResponseMessage());
            BufferedReader reader = new BufferedReader(new InputStreamReader(http.getInputStream()));
            String response = reader.lines().collect(Collectors.joining("\n"));
            ctx.json(response);
        } catch (Exception e) {
            ctx.status(500).result("An error occurred while calling the API");
        }
    }

}