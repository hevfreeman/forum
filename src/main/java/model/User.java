package model;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.InvalidPropertiesFormatException;

/**
 * Created by morev on 10.04.16.
 */
public class User {
    private static final String DEFAULT = "NONE";

    private int uid;

    private String email;

    private String username;

    private String password;

    private String name;

    private String about;

    private boolean isAnonymous;

    public User() {
        uid = -1;
        email = DEFAULT;
        username = DEFAULT;
        password = DEFAULT;
        name = DEFAULT;
        about = DEFAULT;
        isAnonymous = false;
    }

    public User(ResultSet set) throws SQLException{
        /*for(Field f: this.getClass().getFields()){
            f.getName()
        }*/
        uid = set.getInt("id");
        email = set.getString("email");
        username = set.getString("username");
        password = set.getString("password");
        name = set.getString("name");
        about = set.getString("about");
        isAnonymous = set.getBoolean("isAnonymous");
    }

    public User(JsonObject json) throws JsonException{
        uid = json.getInt("id", -1);
        email = json.getString("email", DEFAULT);
        username = json.getString("username", DEFAULT);
        password = json.getString("password", DEFAULT);
        name = json.getString("name", DEFAULT);
        about = json.getString("about", DEFAULT);
        isAnonymous = json.getBoolean("isAnonymous", false);
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public boolean isAnonymous() {
        return isAnonymous;
    }

    public void setAnonymous(boolean anonymous) {
        isAnonymous = anonymous;
    }

    public JsonObject toJson() {
        return Json.createObjectBuilder()
                .add("id", uid)
                .add("email", email)
                .add("name", name)
                .add("about", about)
                .add("isAnonymous", isAnonymous)
                .add("username", username)
                .build();
    }
}
