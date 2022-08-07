package server;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class ResponseSerializer implements JsonSerializer<Response> {
    @Override
    public JsonElement serialize(Response response, Type type,
                                 JsonSerializationContext jsonSerializationContext) {

        JsonObject responseJsonObject = new JsonObject();

        responseJsonObject.addProperty("response", response.getResponse());
        if (response.getResponse().equals("ERROR")) {
            responseJsonObject.addProperty("reason", response.getValue());
        } else {
            responseJsonObject.addProperty("value", response.getValue());
        }

        return responseJsonObject;
    }
}
