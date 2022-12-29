import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.javalin.Javalin;

public class APIRunner
{

    private Gson gson = null;

    public APIRunner(){
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    }

    public static void main(String[] args){
        APIRunner runner = new APIRunner();
        Javalin app = Javalin.create(/*config*/)
                .get("/", ctx -> {ctx.html("MAIN PAGE"); })
                .get("/asset/{nasa_id}", ctx -> {ctx.html("retrieved planet image"); }) // todo:
                .get("/id", ctx -> {ctx.html("DESCRIPTION OF IMAGE"); }) // todo:
                .start(5000);
    }

}
