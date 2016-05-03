package model;

import javax.json.JsonObjectBuilder;

/**
 * Created by morev on 14.04.16.
 */
public interface Jsonable {
    public JsonObjectBuilder toShortJsonObjectBuider();
}
