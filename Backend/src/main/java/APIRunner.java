import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.javalin.Javalin;
import kong.unirest.Unirest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.HashMap;
import java.util.Map;

public class APIRunner {

    private Gson gson = null;

    public APIRunner() {
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    }

    public static void main(String[] args) {
        APIRunner runner = new APIRunner();
        Javalin app = Javalin.create(/*config*/)
                .get("/", ctx -> {
                    ctx.html("MAIN PAGE");
                })
                .get("/earth", ctx -> {

                    //ctx.html("retrieved planet image");


                    Map result = Unirest.get("https://images-api.nasa.gov/search")
                            .queryString("q", "earth")
                            .queryString("media_type", "image")
                            .asObject(i -> new Gson().fromJson(i.getContentReader(), HashMap.class))
                            .getBody();
                    ctx.json(result);

                }) // todo:
                .get("/id2", ctx -> {
                    ctx.html("DESCRIPTION OF IMAGE");
                }) // todo:
                .start(5000);
    }

}
