package client;

import com.beust.jcommander.JCommander;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;


public class Main {
    private static final int PORT = 1445;
    private static final String ADDRESS = "127.0.0.1";

    public static void main(String[] args) throws UnknownHostException {
        System.out.println("Client started!");
        try (
                Socket socket = new Socket(InetAddress.getByName(ADDRESS), PORT);
                DataInputStream input = new DataInputStream(socket.getInputStream());
                DataOutputStream output = new DataOutputStream(socket.getOutputStream());
        ) {
            Request request = new Request();
            JCommander.newBuilder()
                    .addObject(request)
                    .build()
                    .parse(args);
            String requestString = request.toString();
            output.writeUTF(requestString);
            System.out.println("Sent: " + requestString);
            String response = input.readUTF();
            System.out.println("Received: " + response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
