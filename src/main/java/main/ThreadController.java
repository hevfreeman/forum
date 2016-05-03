package main;

import db.ForumTable;
import db.ThreadTable;
import db.UserTable;
import model.Thread;
import model.User;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.json.*;
import java.io.StringReader;
import java.sql.SQLException;

/**
 * Created by morev on 03.05.16.
 */
public class ThreadController {


    @RequestMapping("db/api/thread/create")
    public String create(@RequestBody String jsonString){
        JsonObject threadJson;

        try (JsonReader jsonReader = Json.createReader(new StringReader(jsonString))){
            threadJson = jsonReader.readObject();
        } catch (JsonException e) {
            return ResponseBuilder.getErrorResopnseJsonString(ResponseBuilder.ResponseCode.InvalidRequest,
                    "Cannot parse JSON");
        }

        Thread thread;
        try {
            thread = new Thread(threadJson);
        } catch (JsonException e) {
            return ResponseBuilder.getErrorResopnseJsonString(ResponseBuilder.ResponseCode.IncorrectRequest,
                    "Cannot find required args");
        }

        String user_email;
        String forum_shortname;


        try {
            user_email = UserTable.getEmailById(thread.getUid());
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseBuilder.getErrorResopnseJsonString(ResponseBuilder.ResponseCode.InvalidRequest,
                    "No such user");
        }

        try {
            forum_shortname = ForumTable.getShortnameById(thread.getFid());
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseBuilder.getErrorResopnseJsonString(ResponseBuilder.ResponseCode.InvalidRequest,
                    "No such forum");
        }

        try {
            ThreadTable.create(thread);
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseBuilder.getErrorResopnseJsonString(ResponseBuilder.ResponseCode.UnknownError,
                    "SQL exception");
        }

        JsonObjectBuilder res = thread.toJsonObjectBuider();
        res.add("user", user_email);
        res.add("forum", forum_shortname);

        return ResponseBuilder.getObjectResponseJsonString(res);
    }
}
