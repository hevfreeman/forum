package main;

import com.mysql.jdbc.*;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import controller.ResponseCode;
import db.MyParams;
import model.User;
import org.springframework.web.bind.annotation.*;

import javax.json.*;
import java.io.StringReader;
import java.sql.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by morev on 10.04.16.
 */

@RestController
public class UserController implements MyParams{
    String QUERY_CREATE = "INSERT INTO user(email, username, password, name, about, isAnonymous) " +
            "VALUES (?, ?, ?, ?, ?, ?);";
    String QUERY_SELECT_EMAIL = "SELECT * FROM user WHERE email = ?";

    String QUERY_GET_FOLLOWING = "SELECT uid1 FROM followers WHERE uid2 = ?";
    String QUERY_GET_FOLLOWERS = "SELECT uid2 FROM followers WHERE uid1 = ?";

    Connection conn;


    @RequestMapping("db/api/user/create")
    public String create(@RequestBody String jsonString) {
        System.out.println("UserController.create: " + jsonString);
        JsonObjectBuilder responseBuilder = Json.createObjectBuilder();
        JsonObject userJson;

        try (JsonReader jsonReader = Json.createReader(new StringReader(jsonString))){
            userJson = jsonReader.readObject();
        } catch (JsonException e) {
            responseBuilder.add("code", ResponseCode.InvalidRequest.value());
            responseBuilder.add("response", "Cannot parse JSON");
            String response = responseBuilder.build().toString();
            System.out.println(response);
            return response;
        }

        User user;
        try {
            user = new User(userJson);
        } catch (JsonException e) {
            responseBuilder.add("code", ResponseCode.IncorrectRequest.value());
            responseBuilder.add("response", "Cannot find required args");
            String response = responseBuilder.build().toString();
            System.out.println(response);
            return response;
        }

        try (Connection conn = DriverManager.getConnection(url, login, password);
             PreparedStatement statement = conn.prepareStatement(QUERY_CREATE, Statement.RETURN_GENERATED_KEYS);) {
            statement.setString(1, user.getEmail());
            statement.setString(2, user.getUsername());
            statement.setString(3, user.getPassword()); //TODO WHERE IS IT?
            statement.setString(4, user.getName());
            statement.setString(5, user.getAbout());
            statement.setBoolean(6, user.isAnonymous());

            statement.executeUpdate();
            ResultSet generatedKeys = statement.getGeneratedKeys();
            generatedKeys.next();
            user.setUid(generatedKeys.getInt(1));


            responseBuilder.add("code", ResponseCode.OK.value());
            responseBuilder.add("response",user.toJson());
            String response = responseBuilder.build().toString();
            System.out.println(response);
            return response;
        } catch (MySQLIntegrityConstraintViolationException e) {
            responseBuilder.add("code", ResponseCode.UserAlreadyExists.value());
            responseBuilder.add("response", "User already exists");
            String response = responseBuilder.build().toString();
            System.out.println(response);
            return response;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return responseBuilder.build().toString();
    }


    @RequestMapping(value = "db/api/user/details", method = RequestMethod.GET)
    public String details(@PathVariable String userEmail) {
        System.out.println("UserController.details: " + userEmail);
        JsonObjectBuilder responseBuilder = Json.createObjectBuilder();


        try (Connection conn = DriverManager.getConnection(url, login, password);
             PreparedStatement statement = conn.prepareStatement(QUERY_SELECT_EMAIL);) {
            statement.setString(1, userEmail);
            ResultSet userSet = statement.executeQuery();

            User user = new User(userSet);

            responseBuilder.add("code", ResponseCode.OK.value());
            JsonObjectBuilder userJson = user.toJsonObjectBuider();
            userJson.add("followers");
            userJson.add("following");
            userJson.add("subscriptions");

            responseBuilder.add("response",userJson.build());
            String response = responseBuilder.build().toString();
            System.out.println(response);
            return response;
        } catch (SQLException e) {
            responseBuilder.add("code", ResponseCode.NotFound.value());
            responseBuilder.add("response", "User not found");
            String response = responseBuilder.build().toString();
            System.out.println(response);
            return response;
        }

        String response = responseBuilder.build().toString();
        return  response;
    }



    public ArrayList<String> getFollowersEmail(int uid) {
        try (Connection conn = DriverManager.getConnection(url, login, password);
             PreparedStatement statement = conn.prepareStatement(QUERY_GET_FOLLOWERS);) {
            statement.setInt(1, uid);
            ResultSet uidSet = statement.executeQuery();
            ArrayList<String> res = new ArrayList<>();

            while (uidSet.next()) {
                res.add(uidSet.getString(1));
            }
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

}
