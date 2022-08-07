package client;

import client.Request;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RequestDeserializer implements JsonDeserializer<Request> {

    @Override
    public Request deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

        JsonObject jsonObject = json.getAsJsonObject();
        List<String> keys;

        JsonPrimitive jsonType = jsonObject.getAsJsonPrimitive("type");
        if (jsonType.getAsString().equals("exit")) {
            return new Request(jsonType.getAsString());
        }

        if (jsonObject.get("key").getClass().equals(JsonPrimitive.class)) {
            keys = Arrays.asList(jsonObject.get("key").getAsString());
        } else {
            keys = new Gson().fromJson(jsonObject.get("key"), ArrayList.class);
        }
        var jsonValue = jsonObject.get("value");

        if (jsonType.getAsString().equals("get") || jsonType.getAsString().equals("delete")) {
            return new Request(jsonType.getAsString(), keys);
        } else {
            return new Request(jsonType.getAsString(), keys, jsonValue.toString());
        }
    }
}
