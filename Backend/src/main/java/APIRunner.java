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

    private Gson gson = null;
    private String token;
    private String searchKey;

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
            runner.spotifyLogin(ctx);
        });
    }

    public void spotifyLogin(Context ctx) { ///////////
        String url = "https://api.spotify.com/v1/search";

        if (ctx.req().getHeader("Authorization") == null) {
            token = "";
        } else {
            token = ctx.req().getHeader("Authorization");
        }

        Map result = Unirest.get(url)
                .queryString("q", searchKey)
                .queryString("type", "track")
                .queryString("limit", 5)
                .header("Authorization", token)
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