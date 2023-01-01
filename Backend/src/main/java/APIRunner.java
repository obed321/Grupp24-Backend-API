import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import io.javalin.Javalin;
import io.javalin.http.Context;


;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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