package client;

import java.io.PrintWriter;
import java.net.Socket;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;


public class Client {

    private static int SERVER_PORT;
    private static String SERVER_IP;
    private static String username;

    public static void run(String[] args) throws IOException {
        start(args);
        Socket socket = new Socket(SERVER_IP, SERVER_PORT);

        ServerIn input = new ServerIn(socket);
        ServerOut output = new ServerOut(socket);

        Thread inputThread = new Thread(input);
        Thread outputThread = new Thread(output);

        inputThread.start();
        outputThread.start();

        System.out.println("[CLIENT] Connection to server established");
    }

    public static void start(String[] args) {
        System.out.println("Starting client...");
        String[] serverInfo = args[0].split(":");
        if (serverInfo.length != 2) {
            System.err.println("Start the client with the following format: java Client <serverIP>:<serverPort>");
        }

        // Set server IP
        if (serverInfo[0].equals("localhost")) SERVER_IP = "127.0.0.1";
        else SERVER_IP = serverInfo[0];

        // Set server port
        try {
            SERVER_PORT = Integer.parseInt(serverInfo[1]);
        }
        catch (NumberFormatException e) {
            System.err.println("Start the client with the following format: java Client <serverIP>:<serverPort>");
        }

    }
}