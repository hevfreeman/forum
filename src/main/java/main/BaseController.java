package main;

import controller.ResponseCode;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import java.sql.*;

/**
 * Created by morev on 12.04.16.
 */

@RestController
public class BaseController {
    private static final String QUERY_DISABLE_CHECK = "SET FOREIGN_KEY_CHECKS = 0;";
    private static final String QUERY_ENABLE_CHECK = "SET FOREIGN_KEY_CHECKS = 1;";
    private static final String QUERY_CLEAR = "TRUNCATE user;";

    private static final String QUERY_COUNT_USER = "SELECT COUNT(*) from user;";
    private static final String QUERY_COUNT_THREAD = "SELECT COUNT(*) from thread;";
    private static final String QUERY_COUNT_FORUM = "SELECT COUNT(*) from forum;";
    private static final String QUERY_COUNT_POST = "SELECT COUNT(*) from post;";




    String url = "jdbc:mysql://localhost:3306/TP_FORUM";
    String login = "forum_admin";
    String password = "stargate";

    @RequestMapping(value = "db/api/clear", method = RequestMethod.POST)
    public String clear(@RequestBody String jsonString) {
        System.out.println(this.getClass().getSimpleName() + ".clear");
        JsonObjectBuilder responseBuilder = Json.createObjectBuilder();

        try (Connection conn = DriverManager.getConnection(url, login, password);
             PreparedStatement statement1 = conn.prepareStatement(QUERY_DISABLE_CHECK);
             PreparedStatement statement2 = conn.prepareStatement(QUERY_CLEAR);
             PreparedStatement statement3 = conn.prepareStatement(QUERY_ENABLE_CHECK);) {
             statement1.execute();
             statement2.execute();
             statement3.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        responseBuilder.add("code", ResponseCode.OK.value());
        responseBuilder.add("response", "OK");
        String response = responseBuilder.build().toString();
        System.out.println(response);
        return response;
    }



    @RequestMapping(value = "db/api/status", method = RequestMethod.GET)
    public String count() {
        System.out.println(this.getClass().getSimpleName() + ".count");
        JsonObjectBuilder responseBuilder = Json.createObjectBuilder();

        try (Connection conn = DriverManager.getConnection(url, login, password);
             PreparedStatement statement_user = conn.prepareStatement(QUERY_COUNT_USER);
             PreparedStatement statement_thread = conn.prepareStatement(QUERY_COUNT_THREAD);
             PreparedStatement statement_forum = conn.prepareStatement(QUERY_COUNT_FORUM);
             PreparedStatement statement_post = conn.prepareStatement(QUERY_COUNT_POST);) {


            ResultSet rs_user = statement_user.executeQuery();
            ResultSet rs_thread = statement_thread.executeQuery();
            ResultSet rs_forum = statement_forum.executeQuery();
            ResultSet rs_post = statement_post.executeQuery();

            int count_user = 0;
            int count_thread = 0;
            int count_forum = 0;
            int count_post = 0;


            try {
                rs_user.next();
                rs_thread.next();
                rs_forum.next();
                rs_post.next();

                count_user = rs_user.getInt(1);
                count_thread = rs_thread.getInt(1);
                count_forum = rs_forum.getInt(1);
                count_post = rs_post.getInt(1);
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                rs_user.close();
                rs_thread.close();
                rs_forum.close();
                rs_post.close();
            }


            JsonObjectBuilder builder = Json.createObjectBuilder();
            builder.add("user", count_user);
            builder.add("thread", count_thread);
            builder.add("forum", count_forum);
            builder.add("post", count_post);

            responseBuilder.add("code", ResponseCode.OK.value());
            responseBuilder.add("response", builder);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String response = responseBuilder.build().toString();
        System.out.println(response);
        return response;
    }
}
