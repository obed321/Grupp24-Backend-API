import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.javalin.Javalin;
import io.javalin.http.Context;

;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import io.javalin.plugin.bundled.CorsPluginConfig;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;

import static io.javalin.apibuilder.ApiBuilder.get;

public class APIRunner {

    private Gson gson = null;
    private String token;

    public APIRunner() {
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    }

    public static void main(String[] args) {
        APIRunner runner = new APIRunner();
        Javalin app = Javalin.create(javalinConfig -> javalinConfig.plugins.enableCors(cors -> cors.add(CorsPluginConfig::anyHost))).start(8080);

        app.get("search/{key}", ctx -> {
            runner.searchNasa(ctx);
        });

        app.post("", ctx -> {
            runner.getTokenKey(ctx);
        });

     /*   app.get("spotify", ctx -> { /////////
            runner.spotifyLogin(ctx);
        });*/
    }

    public void spotifyLogin(Context ctx) { ///////////
        String url = "https://api.spotify.com/v1/search";


        if (ctx.req().getHeader("Authorization") == null) {
            token = "";
        } else {
            token = ctx.req().getHeader("Authorization");
        }

        Map result = Unirest.get(url)
                .header("Authorization", token)
                .header("Content-Type", "application/json")
                .asObject(i -> new Gson().fromJson(i.getContentReader(), HashMap.class))
                .getBody();
        ctx.json(result);
        Unirest.shutDown();
    }

    public void getTokenKey(Context ctx) {
        HttpResponse<JsonNode> response = Unirest.post("http://localhost/post")
                .header("accept", "application/json")
                .queryString("apiKey", "123")
                .field("parameter", "value")
                .field("firstname", "Gary")
                .asJson();
    }

    public void searchNasa(Context ctx) {
        String searchKey = ctx.pathParam("key");

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


    //Denna api skickar dagen astronomi bild med text :)
    public void getImageAndDescriptionFromNasaApodAPI(Context ctx) {
        String apiKey = "qUiVgPByEvA7mORI5pfuIyhmmgIcJWNIqf6JYdF0";
        String url = "https://api.nasa.gov/planetary/apod";
        Map<String, String> params = Map.of(
                "api_key", apiKey,
                "count", "1",
                "hd", "true"
        );
        try {
            URL endpoint = new URL(url + "?" + params.entrySet().stream()
                    .map(entry -> entry.getKey() + "=" + entry.getValue())
                    .collect(Collectors.joining("&")));
            HttpURLConnection connection = (HttpURLConnection) endpoint.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String response = reader.lines().collect(Collectors.joining("\n"));
            ctx.json(response);
        } catch (Exception e) {
            ctx.status(500).result("An error occurred while calling the API");
        }

    }

    //Denna metod tar fram hämtar data från endpoint utifrån id
    public void getImageLocation(Context ctx) {
        String id = ctx.pathParam("nasa_id");
        String url = "https://images-api.nasa.gov/metadata/" + id;
        try (InputStream inputStream = new URL(url).openStream()) {
            String data = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));
            ctx.json(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getImageFromNasa(Context ctx) {
        String id = ctx.pathParam("nasa_id");
        String url = "https://images-api.nasa.gov/asset/" + id;
        try (InputStream inputStream = new URL(url).openStream()) {
            String data = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));
            ctx.json(data);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}