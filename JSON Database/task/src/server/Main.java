package server;

import client.Request;
import client.RequestDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class ExitFlag {
    private boolean value = false;

    public synchronized void trip() {
        value = true;
    }

    public synchronized boolean getValue() {
        return value;
    }
}


class Dispatcher extends Thread {
    private Socket socket;
    private Database db;
    private final ExitFlag exitFlag;

    public Dispatcher(Socket socket, Database db, ExitFlag exitFlag) {
        this.socket = socket;
        this.db = db;
        this.exitFlag = exitFlag;
    }

    public void run() {
        try (
                DataInputStream input = new DataInputStream(socket.getInputStream());
                DataOutputStream output = new DataOutputStream(socket.getOutputStream())
        ) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Request.class, new RequestDeserializer())
                    .create();
            Request request = gson.fromJson(input.readUTF(), Request.class);

            Controller controller = new Controller();

            if (request.getType().equals("exit")) {
                output.writeUTF(new Response("OK").toString());
                exitFlag.trip();
            } else if (request.getType().equals("get")) {
                try {
                    Command command = new GetCommand(db, request.getKey(), output);
                    controller.setCommand(command);
                    controller.executeCommand();
                } catch (Exception e) {
                    output.writeUTF(new Response("ERROR", e.getMessage()).toString());
                }
            } else if (request.getType().equals("set")) {
                try {
                    Command command = new SetCommand(db, request.getKey(), request.getValue());
                    controller.setCommand(command);
                    controller.executeCommand();
                    output.writeUTF(new Response("OK").toString());
                } catch (Exception e) {
                    output.writeUTF(new Response("ERROR", e.getMessage()).toString());
                }
            } else if (request.getType().equals("delete")) {
                try {
                    Command command = new DeleteCommand(db, request.getKey());
                    controller.setCommand(command);
                    controller.executeCommand();
                    output.writeUTF(new Response("OK").toString());
                } catch (Exception e) {
                    output.writeUTF(new Response("ERROR", e.getMessage()).toString());
                }
            }
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


public class Main {
    private static final int PORT = 1445;
    private static final String ADDRESS = "127.0.0.1";

    public static void main(String[] args) {

        System.out.println("Server started!");
        ExitFlag exitFlag = new ExitFlag();
        ExecutorService executor = Executors.newCachedThreadPool();

        try {
            Database db = new Database();
            ServerSocket serverSocket = new ServerSocket(PORT);
            serverSocket.setSoTimeout(20);

            while (!exitFlag.getValue()) {
                try {
                    Dispatcher dispatcher = new Dispatcher(serverSocket.accept(), db, exitFlag);
                    executor.submit(dispatcher);
                } catch (SocketTimeoutException ignored) {}
            }
            executor.shutdown();
            while (!executor.isTerminated()) {
                Thread.sleep(20);
            }
            serverSocket.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
