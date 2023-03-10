package client;

import java.io.PrintWriter;
import java.net.Socket;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;


public class Client {

    private static final int SERVER_PORT = 9090;
    private static final String SERVER_IP = "127.0.0.1";
    private String username;

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket(SERVER_IP, SERVER_PORT);

        ServerIn input = new ServerIn(socket);
        ServerOut output = new ServerOut(socket);

        Thread inputThread = new Thread(input);
        Thread outputThread = new Thread(output);

        inputThread.start();
        outputThread.start();

        System.out.println("[CLIENT] Connection to server established");
    }
}