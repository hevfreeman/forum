package db;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import main.ResponseBuilder;
import model.Forum;
import model.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import java.sql.*;
import java.util.ArrayList;

/**
 * Created by morev on 03.05.16.
 */
public class ForumTable {
    static final String QUERY_CREATE =
            "INSERT INTO forum(shortname, name, uid) " +
                    "VALUES (?, ?, ?);";

    static final String QUERY_SELECT_SHORTNAME =
            "SELECT * FROM forum WHERE shortname = ?";

    static final String QUERY_GET_SHORTNAME_BY_ID =
            "SELECT shortname FROM forum WHERE fid = ?";

    public static String getShortnameById(int id) throws SQLException{
        try (Connection conn = DriverManager.getConnection(MyParams.url, MyParams.login, MyParams.password);
             PreparedStatement statement = conn.prepareStatement(QUERY_GET_SHORTNAME_BY_ID);) {
            statement.setInt(1, id);
            ResultSet set = statement.executeQuery();
            set.next();
            String shortname = set.getString(1);
            set.close();
            return shortname;
        } catch (SQLException e) {
            throw e;
        }
    }


    public static int create(Forum forum) throws SQLException {
        try (Connection conn = DriverManager.getConnection(MyParams.url, MyParams.login, MyParams.password);
             PreparedStatement statement = conn.prepareStatement(QUERY_CREATE, Statement.RETURN_GENERATED_KEYS);) {
            statement.setString(1, forum.getShortname());
            statement.setString(2, forum.getName());
            statement.setInt(3, forum.getUid());

            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            generatedKeys.next();
            int id = generatedKeys.getInt(1);
            forum.setFid(id);
            generatedKeys.close();

            return id;
        } catch (MySQLIntegrityConstraintViolationException e) {
            return -1;
        } catch (SQLException e) {
            throw e;
        }
    }

    public static Forum getForumByShortname(String shortname) throws SQLException{
        try (Connection conn = DriverManager.getConnection(MyParams.url, MyParams.login, MyParams.password);
             PreparedStatement statement = conn.prepareStatement(QUERY_SELECT_SHORTNAME);) {
            statement.setString(1, shortname);
            ResultSet forumSet = statement.executeQuery();

            Forum forum = new Forum(forumSet);
            forumSet.close();
            return forum;
        } catch (SQLException e) {
            throw e;
        }
    }
}
