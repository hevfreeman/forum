package main;

import main.ResponseCode;
import model.User;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.json.*;
import java.io.StringReader;
import java.sql.*;

/**
 * Created by morev on 10.04.16.
 */

@RestController
public class UserController {

    @RequestMapping("db/api/user/create")
    public String create(@RequestBody String jsonString) {
        JsonObjectBuilder responseBuilder = Json.createObjectBuilder();
        JsonObject userJson;
        User user = new User();

        try (JsonReader jsonReader = Json.createReader(new StringReader(jsonString))){
            userJson = jsonReader.readObject();
        } catch (JsonException e) {
            responseBuilder.add("code", ResponseCode.InvalidRequest.value());
            responseBuilder.add("response", "Cannot parse JSON");
            return responseBuilder.build().toString();
        }

        try {
            user.setUsername(userJson..getString("username"));
            user.setAbout(userJson.getString("about"));
            user.setName(userJson.getString("name"));
            user.setEmail(userJson.getString("email"));

            user.setAnonymous(userJson.getBoolean("isAnonymous", false));
        } catch (JsonException e) {
            responseBuilder.add("code", ResponseCode.IncorrectRequest.value());
            responseBuilder.add("response", "Cannot find required args");
            return responseBuilder.build().toString();
        }


        try {
            String url = "jdbc:mysql://localhost:3306/TP_FORUM";
            String login = "root";
            String password = "12700";



            Connection conn = DriverManager.getConnection(url, login, password);
            String CREATE_QUERY = "INSERT INTO user(email, username, password, name, about, isAnonymous) VALUES (?, ?, ?, ?, ?, ?);";
            PreparedStatement statement = conn.prepareStatement(CREATE_QUERY);
            statement.setString(1, user.getEmail());
            statement.setString(2, user.getUsername());
            statement.setString(3, user.getPassword()); //TODO WHERE IS IT?
            statement.setString(4, user.getName());
            statement.setString(5, user.getAbout());
            statement.setBoolean(6, user.isAnonymous());

            statement.execute();

            /*String GET_USER_QUERY = "SELECT * FROM user WHERE email = ?";
            PreparedStatement getStatement = conn.prepareStatement(GET_USER_QUERY);
            ResultSet isUserCreated = getStatement.executeQuery();
            if (isUserCreated.next()) {
                responseBuilder.add("code", ResponseCode.UserAlreadyExists.value());
                responseBuilder.add("response", "User already exists");
                return responseBuilder.build().toString();

            }
            int numberOfRowsAffected = statement.executeUpdate();
            if (numberOfRowsAffected == 0) {
                responseBuilder.add("code", ResponseCode.UserAlreadyExists.value());
                responseBuilder.add("response", "User already exists");
                return responseBuilder.build().toString();
            } else {
                getStatement = conn.prepareStatement(GET_USER_QUERY);
                getStatement.setString(1, user.getEmail());
                ResultSet newUser = getStatement.executeQuery();
                if (newUser.next()) {
                    responseBuilder.add("code", ResponseCode.OK.toString());
                    responseBuilder.add("response",(new User(newUser).toJson()));
                    return responseBuilder.toString();
                }
            }*/
            responseBuilder.add("code", ResponseCode.OK.toString());
            responseBuilder.add("response",user.toJson());
            return responseBuilder.toString();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return responseBuilder.build().toString();
    }

}
