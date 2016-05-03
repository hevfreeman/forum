package main;

import db.MyParams;
import db.UserTable;
import model.User;
import org.springframework.web.bind.annotation.*;

import javax.json.*;
import java.io.StringReader;
import java.sql.*;
import java.util.ArrayList;

/**
 * Created by morev on 10.04.16.
 */

@RestController
public class UserController implements MyParams{

    @RequestMapping("db/api/user/create")
    public String create(@RequestBody String jsonString){
        System.out.println("UserController.create: " + jsonString);

        JsonObject userJson;

        try (JsonReader jsonReader = Json.createReader(new StringReader(jsonString))){
            userJson = jsonReader.readObject();
        } catch (JsonException e) {
            return ResponseBuilder.getErrorResopnseJsonString(ResponseBuilder.ResponseCode.InvalidRequest,
                    "Cannot parse JSON");
        }

        User user;
        try {
            user = new User(userJson);
        } catch (JsonException e) {
            return ResponseBuilder.getErrorResopnseJsonString(ResponseBuilder.ResponseCode.IncorrectRequest,
                    "Cannot find required args");
        }

        try {
            UserTable.createUser(user);
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseBuilder.getErrorResopnseJsonString(ResponseBuilder.ResponseCode.UnknownError,
                    "SQL exception");
        }

        return ResponseBuilder.getObjectResponseJsonString(user);
    }


    @RequestMapping(value = "db/api/user/details", method = RequestMethod.GET)
    public String details(@RequestParam("user") String email) {
        System.out.println("UserController.details: " + email);
        JsonObjectBuilder responseBuilder = Json.createObjectBuilder();

        try {
            User user = UserTable.getUserByEmail(email);

            JsonObjectBuilder userJson = user.toShortJsonObjectBuider();

            JsonArrayBuilder followersBuilder = Json.createArrayBuilder();
            JsonArrayBuilder followingBuilder = Json.createArrayBuilder();
            JsonArrayBuilder subscriptionsBuilder = Json.createArrayBuilder();

            ArrayList<String> followerList = UserTable.getFollowersEmails(email);
            ArrayList<String> followingList = UserTable.getFollowingEmails(email);
            ArrayList<Integer> subscriptionsList = UserTable.getSubscritions(email);

            for (String follower_email: followerList)
                followersBuilder.add(follower_email);

            for (String following_email: followingList)
                followingBuilder.add(following_email);

            for (Integer subscription_id: subscriptionsList)
                subscriptionsBuilder.add(subscription_id);

            userJson.add("followers", followersBuilder.build());
            userJson.add("following", followingBuilder.build());
            userJson.add("subscriptions", subscriptionsBuilder.build());

            responseBuilder.add("response",userJson.build());
            String response = responseBuilder.build().toString();
            System.out.println(response);
            return response;
        } catch (SQLIntegrityConstraintViolationException e) {
            return ResponseBuilder.getErrorResopnseJsonString(ResponseBuilder.ResponseCode.NotFound,
                    "No such user");
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseBuilder.getErrorResopnseJsonString(ResponseBuilder.ResponseCode.UnknownError,
                    "SQL exception");
        }
    }


    @RequestMapping("db/api/user/follow")
    public String follow(@RequestBody String jsonString){
        System.out.println("UserController.follow: " + jsonString);

        JsonObject json;
        String followee, follower;

        try (JsonReader jsonReader = Json.createReader(new StringReader(jsonString))){
            json = jsonReader.readObject();
            followee = json.getString("followee");
            follower = json.getString("follower");
        } catch (JsonException e) {
            return ResponseBuilder.getErrorResopnseJsonString(ResponseBuilder.ResponseCode.InvalidRequest,
                    "Cannot parse JSON");
        }

        try {
            followee = json.getString("followee");
            follower = json.getString("follower");
        } catch (JsonException e) {
            return ResponseBuilder.getErrorResopnseJsonString(ResponseBuilder.ResponseCode.IncorrectRequest,
                    "Cannot find required args");
        }

        try {
            UserTable.follow(followee, follower);
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseBuilder.getErrorResopnseJsonString(ResponseBuilder.ResponseCode.UnknownError,
                    "SQL exception");
        }

        return details(followee);
    }


    @RequestMapping("db/api/user/unfollow")
    public String unfollow(@RequestBody String jsonString){
        System.out.println("UserController.unfollow: " + jsonString);

        JsonObject json;
        String followee, follower;

        try (JsonReader jsonReader = Json.createReader(new StringReader(jsonString))){
            json = jsonReader.readObject();
            followee = json.getString("followee");
            follower = json.getString("follower");
        } catch (JsonException e) {
            return ResponseBuilder.getErrorResopnseJsonString(ResponseBuilder.ResponseCode.InvalidRequest,
                    "Cannot parse JSON");
        }

        try {
            followee = json.getString("followee");
            follower = json.getString("follower");
        } catch (JsonException e) {
            return ResponseBuilder.getErrorResopnseJsonString(ResponseBuilder.ResponseCode.IncorrectRequest,
                    "Cannot find required args");
        }

        try {
            UserTable.unfollow(followee, follower);
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseBuilder.getErrorResopnseJsonString(ResponseBuilder.ResponseCode.UnknownError,
                    "SQL exception");
        }

        return details(followee);
    }


    @RequestMapping("db/api/user/updateProfile")
    public String updateProfile(@RequestBody String jsonString) {
        System.out.println("UserController.updateProfile: " + jsonString);

        JsonObject json;
        String about, email, name;

        try (JsonReader jsonReader = Json.createReader(new StringReader(jsonString))){
            json = jsonReader.readObject();
        } catch (JsonException e) {
            return ResponseBuilder.getErrorResopnseJsonString(ResponseBuilder.ResponseCode.InvalidRequest,
                    "Cannot parse JSON");
        }

        try {
            about = json.getString("about");
            email = json.getString("user");
            name = json.getString("name");
        } catch (JsonException e) {
            return ResponseBuilder.getErrorResopnseJsonString(ResponseBuilder.ResponseCode.IncorrectRequest,
                    "Cannot find required args");
        }
        try {
            UserTable.update(email, name, about);
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseBuilder.getErrorResopnseJsonString(ResponseBuilder.ResponseCode.UnknownError,
                    "SQL exception");
        }
        return details(email);
    }


    @RequestMapping(value = "db/api/user/listFollowers", method = RequestMethod.GET)
    public String listFollowers(@RequestParam(value = "user", required = true) String email,
                                @RequestParam(value = "limit", required = false) int limit,
                                @RequestParam(value = "order", required = false) String order,
                                @RequestParam(value = "since_id", required = false) int since_id) {
        System.out.println("UserController.updateProfile: " +  email);
        /*ArrayList<String> followersEmails = getStringArray(QUERY_GET_FOLLOWERS_EMAIL, user, order, limit, since_id);

        JsonArrayBuilder arr = Json.createArrayBuilder();
        try {
            for (String email : followersEmails) {
                arr.add(getUser(email).toShortJsonObjectBuider().build().toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        responseBuilder.add("code", ResponseBuilder.ResponseCode.OK.value());
        responseBuilder.add("response", arr.build());
        String response = responseBuilder.build().toString();
        System.out.println(response);
        return response;*/
        return "";
    }

    /*
    @RequestMapping(value = "db/api/user/listFollowing", method = RequestMethod.GET)
    public String listFollowing(@PathVariable String user, @PathVariable String order,
                                @PathVariable int limit, @PathVariable int since_id) {
        JsonObjectBuilder responseBuilder = Json.createObjectBuilder();

        ArrayList<String> followingEmails = getStringArray(QUERY_GET_FOLLOWING_EMAIL, user, order, limit, since_id);

        JsonArrayBuilder arr = Json.createArrayBuilder();
        try {
            for (String email : followingEmails) {
                arr.add(getUser(email).toShortJsonObjectBuider().build().toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        responseBuilder.add("code", ResponseBuilder.ResponseCode.OK.value());
        responseBuilder.add("response", arr.build());
        String response = responseBuilder.build().toString();
        System.out.println(response);
        return response;
    }


    @RequestMapping(value = "db/api/user/listPosts", method = RequestMethod.GET)
    public String listPosts(@PathVariable String user, @PathVariable String order,
                                @PathVariable int limit, @PathVariable int since_id) {

    }
    */
}
