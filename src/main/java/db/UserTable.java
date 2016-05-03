package db;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import main.ResponseBuilder;
import model.User;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import java.sql.*;
import java.util.ArrayList;

/**
 * Created by morev on 14.04.16.
 */
public class UserTable {
    static final String QUERY_CREATE =
            "INSERT INTO user(email, username, password, name, about, isAnonymous) " +
                    "VALUES (?, ?, ?, ?, ?, ?);";

    static final String QUERY_UPDATE =
            "UPDATE user " +
                    "SET name = ?, about = ? " +
                    "WHERE email = ?;";

    static final String QUERY_GET_EMAIL_BY_ID =
            "SELECT email FROM user WHERE uid = ?";

    static final String QUERY_SELECT_EMAIL =
            "SELECT * FROM user WHERE email = ?";

    static final String QUERY_FOLLOW =
            "INSERT INTO followers(uid1, uid2) " +
                    "SELECT u1.uid, u2.uid FROM user AS u1 " +
                    "JOIN user AS u2 " +
                    "WHERE u1.email = ? " +
                    "AND u2.email = ?;";

    static final String QUERY_UNFOLLOW =
            "DELETE FROM followers " +
                    "WHERE uid1 IN " +
                    "(SELECT uid FROM user WHERE email = ?)" +
                    "AND uid2 IN " +
                    "(SELECT uid FROM user WHERE email = ?)";

    static final String QUERY_GET_FOLLOWING_EMAILS =
            "SELECT fu.email FROM followers AS f " +
                    "JOIN user AS u ON f.uid1=u.uid " +
                    "JOIN user AS fu ON f.uid2=fu.uid " +
                    "WHERE u.email = ?;";

    static final String QUERY_GET_FOLLOWERS_EMAILS =
            "SELECT fu.email FROM followers AS f " +
                    "JOIN user AS u ON f.uid2=u.uid " +
                    "JOIN user AS fu ON f.uid1=fu.uid " +
                    "WHERE u.email = ?;";

    static final String QUERY_GET_SUBSCRIPTIONS =
            "SELECT s.tid FROM subscriptions AS s " +
                    "JOIN user AS u ON s.uid=u.uid " +
                    "WHERE u.email = ?;";

    static final String QUERY_GET_FOLLOWERS =
            "SELECT ";

    static final int DEFAULT_LIMIT = 1000;

    public static int createUser(User user) throws SQLException{
        try (Connection conn = DriverManager.getConnection(MyParams.url, MyParams.login, MyParams.password);
             PreparedStatement statement = conn.prepareStatement(QUERY_CREATE, Statement.RETURN_GENERATED_KEYS);) {
            statement.setString(1, user.getEmail());
            statement.setString(2, user.getUsername());
            statement.setString(3, user.getPassword());
            statement.setString(4, user.getName());
            statement.setString(5, user.getAbout());
            statement.setBoolean(6, user.isAnonymous());

            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            generatedKeys.next();
            int id = generatedKeys.getInt(1);
            user.setUid(id);
            generatedKeys.close();

            return id;

        } catch (MySQLIntegrityConstraintViolationException e) {
            return -1;
        } catch (SQLException e) {
            throw e;
        }
    }

    public static User getUserByEmail(String email) throws SQLException{
        try (Connection conn = DriverManager.getConnection(MyParams.url, MyParams.login, MyParams.password);
             PreparedStatement statement = conn.prepareStatement(QUERY_SELECT_EMAIL);) {
            statement.setString(1, email);
            ResultSet userSet = statement.executeQuery();
            User user = new User(userSet);
            userSet.close();
            return user;
        } catch (SQLException e) {
            throw e;
        }
    }

    public static String getEmailById(int id) throws SQLException{
        try (Connection conn = DriverManager.getConnection(MyParams.url, MyParams.login, MyParams.password);
             PreparedStatement statement = conn.prepareStatement(QUERY_GET_EMAIL_BY_ID);) {
            statement.setInt(1, id);
            ResultSet set = statement.executeQuery();
            set.next();
            String email = set.getString(1);
            set.close();
            return email;
        } catch (SQLException e) {
            throw e;
        }
    }


    public static boolean follow(String email, String followers_email) throws SQLException{
        try (Connection conn = DriverManager.getConnection(MyParams.url, MyParams.login, MyParams.password);
             PreparedStatement statement = conn.prepareStatement(QUERY_FOLLOW)) {
            statement.setString(1, email);
            statement.setString(2, followers_email);

            statement.executeUpdate();

            return true;

        } catch (MySQLIntegrityConstraintViolationException e) {
            return false;
        } catch (SQLException e) {
            throw e;
        }
    }

    public static boolean unfollow(String email, String followers_email) throws SQLException{
        try (Connection conn = DriverManager.getConnection(MyParams.url, MyParams.login, MyParams.password);
             PreparedStatement statement = conn.prepareStatement(QUERY_UNFOLLOW)) {
            statement.setString(1, email);
            statement.setString(2, followers_email);

            statement.executeUpdate();

            return true;

        } catch (SQLException e) {
            throw e;
        }
    }


    public static void update(String email, String name, String about) throws SQLException {
        try (Connection conn = DriverManager.getConnection(MyParams.url, MyParams.login, MyParams.password);
             PreparedStatement statement = conn.prepareStatement(QUERY_UPDATE)) {
            statement.setString(1, name);
            statement.setString(2, about);
            statement.setString(3, email);

            statement.executeUpdate();

        } catch (SQLException e) {
            throw e;
        }
    }


    public static ArrayList<String> getFollowersEmails(String email) throws SQLException{
        ArrayList<String> followersEmails;
        try {
            followersEmails = getStringArray(QUERY_GET_FOLLOWERS_EMAILS, email);
        } catch (SQLException e) {
            throw e;
        }
        return followersEmails;
    }

    public static ArrayList<String> getFollowingEmails(String email) throws SQLException{
        ArrayList<String> followingEmails;
        try {
            followingEmails = getStringArray(QUERY_GET_FOLLOWING_EMAILS, email);
        } catch (SQLException e) {
            throw e;
        }
        return followingEmails;
    }

    public static ArrayList<Integer> getSubscritions(String email) throws SQLException{
        ArrayList<Integer> subscribedThreads;
        try {
            subscribedThreads = getIntArray(QUERY_GET_SUBSCRIPTIONS, email);
        } catch (SQLException e) {
            throw e;
        }
        return subscribedThreads;
    }

    /*
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
*/

















    // GENERAL METHOD

    public static ArrayList<String> getStringArray(String query, String email) throws SQLException{ //TODO max_id?
        try (Connection conn = DriverManager.getConnection(MyParams.url, MyParams.login, MyParams.password);
             PreparedStatement statement = conn.prepareStatement(query);) {
            statement.setString(1, email);
            ResultSet emailSet = statement.executeQuery();
            ArrayList<String> res = new ArrayList<>();

            while (emailSet.next())
                res.add(emailSet.getString(1));

            return res;
        } catch (SQLException e) {
            throw  e;
        }
    }

    public static ArrayList<Integer> getIntArray(String query, String email) throws SQLException{
        try (Connection conn = DriverManager.getConnection(MyParams.url, MyParams.login, MyParams.password);
             PreparedStatement statement = conn.prepareStatement(query);) {
            statement.setString(1, email);
            ResultSet emailSet = statement.executeQuery();
            ArrayList<Integer> res = new ArrayList<>();

            while (emailSet.next())
                res.add(emailSet.getInt(1));

            return res;
        } catch (SQLException e) {
            throw  e;
        }
    }
}
