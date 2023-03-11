package client;

import server.ServerProtocol;

import java.lang.reflect.Array;
import java.net.Socket;
import java.io.IOException;


public class Client {
    // Server info
    private static int SERVER_PORT;
    private static String SERVER_IP;
    private Socket socket;

    // Input and output streams
    private ServerIn inputSocket;
    private ServerOut outputSocket;
    private Thread inputThread;
    private Thread outputThread;

    // Username
    private static String username = System.getProperty("user.name");

    public void run(String[] args) throws IOException {
        start(args);
        this.socket = new Socket(SERVER_IP, SERVER_PORT);

        this.inputSocket = new ServerIn(socket, this);
        this.outputSocket = new ServerOut(socket, this);

        this.inputThread = new Thread(this.inputSocket);
        this.outputThread = new Thread(this.outputSocket);

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

    protected String getUsername() {
        return username;
    }
}