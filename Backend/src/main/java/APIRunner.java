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

public class APIRunner
{

    private Gson gson = null;

    public APIRunner(){
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    }

    public static void main(String[] args){
        APIRunner runner = new APIRunner();
        Javalin app = Javalin.create().start(5000);
        app.get("/", ctx -> {ctx.html("MAIN PAGE"); });




        app.get("metadata/{nasa_id}", ctx -> {
            String id = ctx.pathParam("nasa_id");
            String url = "https://images-api.nasa.gov/metadata/" + id;
            try (InputStream inputStream = new URL(url).openStream()) {
                String data = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));
                ctx.result(data);
             //   ctx.header("Accept");
                //ctx.header("Content-Type", "application/json");
               Map map = new HashMap<String, String>();
            //    map.put("id", id);
                map.put("Description ", data + id);
                ctx.json(map);


            } catch (IOException e) {
                e.printStackTrace();
            }
        });


    }



              //  app.get("/asset/{nasa_id}", ctx -> {ctx.html("retrieved planet image"); }); // todo:

     //   Map map = new HashMap<String, String>();
     //   map.put("Description", "Infomation " + ctx.pathParam("id"));
     //   ctx.json(map);
    }
