package db;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import model.Thread;
import model.User;

import java.sql.*;

/**
 * Created by morev on 03.05.16.
 */
public class ThreadTable {
    static final String QUERY_CREATE =
            "INSERT INTO thread(date, slug, title, likes, dislikes, isClosed, isDeleted, uid, fid, message) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

    public static int create(Thread thread) throws SQLException {
        try (Connection conn = DriverManager.getConnection(MyParams.url, MyParams.login, MyParams.password);
             PreparedStatement statement = conn.prepareStatement(QUERY_CREATE, Statement.RETURN_GENERATED_KEYS);) {
            statement.setTimestamp(1, thread.getDate());
            statement.setString(2, thread.getSlug());
            statement.setString(3, thread.getTitle());
            statement.setInt(4, thread.getLikes());
            statement.setInt(5, thread.getDislikes());
            statement.setBoolean(6, thread.isClosed());
            statement.setBoolean(7, thread.isDeleted());
            statement.setInt(8, thread.getUid());
            statement.setInt(9, thread.getFid());
            statement.setString(10, thread.getMessage());

            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            generatedKeys.next();
            int id = generatedKeys.getInt(1);
            thread.setTid(id);
            generatedKeys.close();

            return id;
        } catch (MySQLIntegrityConstraintViolationException e) {
            return -1;
        } catch (SQLException e) {
            throw e;
        }
    }
}
