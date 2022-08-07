package server;

import com.google.gson.*;


public class Response {
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(Response.class, new ResponseSerializer())
            .create();

    private String response;

    private String value;

    public Response(String response) {
        this.response = response;
    }

    public Response(String response, String value) {
        this.response = response;
        this.value = value;
    }

    public String getResponse() {
        return response;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return gson.toJson(this);
    }
}

