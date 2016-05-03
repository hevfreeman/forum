package main;

import model.Jsonable;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 * Created by morev on 14.04.16.
 */
public class ResponseBuilder {
    public static String getObjectResponseJsonString(Jsonable obj) {
        JsonObjectBuilder responseBuilder = Json.createObjectBuilder();

        responseBuilder.add("code", ResponseBuilder.ResponseCode.OK.value());
        responseBuilder.add("response", obj.toShortJsonObjectBuider());
        String response = responseBuilder.build().toString();
        System.out.println(response);

        return response;
    }

    public static String getErrorResopnseJsonString(ResponseCode code, String message) {
        JsonObjectBuilder responseBuilder = Json.createObjectBuilder();

        responseBuilder.add("code", code.value());
        responseBuilder.add("response", message);
        String response = responseBuilder.build().toString();
        System.out.println(response);

        return response;
    }

    public static String getObjectResponseJsonString(JsonObjectBuilder jsonObjectBuilder) {
        JsonObjectBuilder responseBuilder = Json.createObjectBuilder();

        responseBuilder.add("code", ResponseBuilder.ResponseCode.OK.value());
        responseBuilder.add("response", jsonObjectBuilder);
        String response = responseBuilder.build().toString();
        System.out.println(response);

        return response;
    }




    /**
     * Created by morev on 10.04.16.
     */
    public static enum ResponseCode {
        OK(0),
        NotFound(1),
        InvalidRequest(2),
        IncorrectRequest(3),
        UnknownError(4),
        UserAlreadyExists(5);

        private final int code;

        ResponseCode(int code) {
            this.code = code;
        }

        public int value() {
            return code;
        }
    }
}
