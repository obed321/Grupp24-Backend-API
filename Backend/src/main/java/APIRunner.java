import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import io.javalin.http.Context;

import org.jetbrains.annotations.NotNull;

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

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;

public class APIRunner {

    private Gson gson = null;

    public APIRunner() {
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    }

    public static void main(String[] args) {
        APIRunner runner = new APIRunner();
        Javalin app = Javalin.create().start(5000);
        app.get("/", ctx -> {ctx.html("MAIN PAGE");});
        app.get("metadata/{nasa_id}", ctx -> {runner.getImageFromNasa(ctx);});

        //Använd denna api om du vill få picture of the day från nasa
        app.get("/<version>/apod", ctx -> {runner.getImageAndDescriptionFromNasaApodAPI(ctx);});
    }



//Denna api skickar dagen astronomi bild med text :)
public void getImageAndDescriptionFromNasaApodAPI (Context ctx) {
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
    public void getImageFromNasa(Context ctx) {
        String id = ctx.pathParam("nasa_id");
        String url = "https://images-api.nasa.gov/metadata/" + id;
        try (InputStream inputStream = new URL(url).openStream()) {
            String data = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));
            ctx.json(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }






}