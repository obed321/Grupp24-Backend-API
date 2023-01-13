import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.javalin.Javalin;
import io.javalin.http.Context;
import kong.unirest.Unirest;
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
                .get("/planet/{string}", ctx -> {
                    runner.searchFunc(ctx);
                })
                .start(5000);
    }

    public void searchFunc(Context ctx){
        String search = ctx.pathParam("string");

        Map result = Unirest.get("https://images-api.nasa.gov/search")
                .queryString("q", search)
                .queryString("media_type", "image")
                .asObject(i -> new Gson().fromJson(i.getContentReader(), HashMap.class))
                .getBody();
        ctx.json(result);
        Unirest.shutDown();
    }
}
