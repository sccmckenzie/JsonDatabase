package server;


import java.io.DataOutputStream;
import java.util.List;

interface Command {
    void execute() throws Exception;
}

class GetCommand implements Command {
    private Database database;
    private List<String> key;
    private DataOutputStream output;

    GetCommand(Database database, List<String> key, DataOutputStream output) {
        this.database = database;
        this.key = key;
        this.output = output;
    }

    @Override
    public void execute() throws Exception {
        output.writeUTF(new Response("OK", database.get(key)).toString().replaceAll("\\\\", "").replaceAll("\\\\", "").replaceAll("\"\\{", "{").replaceAll("\\}\"", "}").replaceAll("\"\"", "\""));
    }
}

class SetCommand implements Command {
    private Database database;
    private List<String> key;
    private String value;

    SetCommand(Database database, List<String> key, String value) {
        this.database = database;
        this.key = key;
        this.value = value;
    }

    @Override
    public void execute() throws Exception {
        database.set(key, value);
    }
}

class DeleteCommand implements Command {
    private Database database;
    private List<String> key;

    DeleteCommand(Database database, List<String> key) {
        this.database = database;
        this.key = key;
    }

    @Override
    public void execute() throws Exception {
        database.delete(key);
    }
}