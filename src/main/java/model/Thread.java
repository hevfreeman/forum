package model;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Created by morev on 03.05.16.
 */
public class Thread {
    private int tid;
    private Timestamp date;
    private String slug;
    private String title;
    private int likes;
    private int dislikes ;
    private boolean isClosed;
    private boolean isDeleted;
    private int uid;
    private int fid;
    private String message;




    public Thread() {
        tid = -1;
        date = null;
        slug = null;
        title = null;
        likes = 0;
        dislikes = 0;
        isClosed = false;
        isDeleted = false;
        uid = -1;
        fid = -1;
        message = null;
    }

    public int getTid() {
        return tid;
    }

    public Timestamp getDate() {
        return date;
    }

    public String getSlug() {
        return slug;
    }

    public String getTitle() {
        return title;
    }

    public int getLikes() {
        return likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public int getUid() {
        return uid;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }

    public int getFid() {
        return fid;

    }

    public String getMessage() {
        return message;
    }

    public Thread(ResultSet set) throws SQLException {
        set.next();
        tid = set.getInt("tid");
        date = set.getTimestamp("date");
        slug = set.getString("slug");
        title = set.getString("title");
        likes = set.getInt("likes");
        dislikes = set.getInt("dislikes");
        isClosed = set.getBoolean("isClosed");
        isDeleted = set.getBoolean("isDeleted");
        uid = set.getInt("uid");
        fid = set.getInt("fid");
        message = set.getString("message");

    }

    public Thread(JsonObject json) throws JsonException {
        tid = json.getInt("tid", -1);
        date = Timestamp.valueOf(json.getString("date")); //
        slug = json.getString("slug"); //
        title = json.getString("title"); //
        likes = json.getInt("likes", 0);
        dislikes = json.getInt("dislikes", 0);
        isClosed = json.getBoolean("isClosed"); //
        isDeleted = json.getBoolean("isDeleted", false); //optional
        uid = json.getInt("uid"); //via email
        fid = json.getInt("fid"); //via shortname
        message = json.getString("message"); //
    }

    public JsonObjectBuilder toJsonObjectBuider() {
        return Json.createObjectBuilder()
                .add("date", date.toString())
                .add("id", tid)
                .add("isClosed", isClosed)
                .add("isDeleted", isDeleted)
                .add("message", message)
                .add("slug", slug)
                .add("title", title);
    }
}
