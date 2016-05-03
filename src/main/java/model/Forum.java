package model;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by morev on 03.05.16.
 */
public class Forum {
    private int fid;
    private String shortname;
    private String name;
    private int uid;

    public Forum() {
        fid = -1;
        shortname = null;
        name = null;
        uid = -1;
    }

    public int getFid() {
        return fid;
    }

    public void setFid(int fid) {
        this.fid = fid;
    }

    public String getShortname() {
        return shortname;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public Forum(ResultSet set) throws SQLException {
        set.next();
        fid = set.getInt("fid");
        shortname = set.getString("shortname");
        name = set.getString("name");
        uid = set.getInt("uid");
    }

    public Forum(JsonObject json) throws JsonException {
        fid = json.getInt("fid", -1);
        shortname = json.getString("short_name");
        name = json.getString("name");
        uid = json.getInt("uid", -1);
    }

    public JsonObjectBuilder toJsonObjectBuider() {
        return Json.createObjectBuilder()
                .add("id", fid)
                .add("name", name)
                .add("short_name", shortname);
    }
}
