package client;

import com.beust.jcommander.Parameter;
import com.google.gson.Gson;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;


public class Request {
    @Parameter(names = "-t", description = "client.Request type")
    private String type;

    @Parameter(names = "-k", description = "client.Request key")
    private List<String> key;

    @Parameter(names = "-v", description = "client.Request value")
    private String value;

    @Parameter(names = "-in", description = "client.Request filepath")
    private String filepath;

    public Request() {}

    public Request(String type, List<String> key, String value) {
        this.type = type;
        this.key = key;
        this.value = value;
    }

    public Request(String type, List<String> key) {
        this.type = type;
        this.key = key;
    }

    public Request(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public List<String> getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        if (this.filepath != null) {
            try {
                return new String(Files.readAllBytes(Paths.get("C:/Users/Scott/IdeaProjects/JSON Database/JSON Database/task/src/client/data/" + filepath)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new Gson().toJson(this).replaceAll("key\":\\[", "key\":").replaceAll("\\],\"value", ",\"value");
    }
}