package client;

import server.ServerProtocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Sends commands from client to server.*/
public class ServerOut implements Runnable{

    private final Socket serverSocket;
    private final PrintWriter out;
    private final BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
    private final Client client;

    private Boolean running = true;

    /**
     * Creates an instance of ServerOut*/
    public ServerOut(Socket serverSocket, Client client) throws IOException {
        this.serverSocket = serverSocket;
        this.out = new PrintWriter(this.serverSocket.getOutputStream(), true);
        this.client = client;
    }

    /**
     * From the Runnable interface. Runs the ServerOut thread to send commands to the server
     */
    @Override
    public void run() {
        try {
            while(this.running) {
                System.out.print("> ");
                String command = this.keyboard.readLine();

                int firstSpace = command.indexOf(" ");

                if (command.equals("quit")) {
                    this.running = false;
                }
                if (firstSpace != -1) {
                    if (command.startsWith("say")) {
                        System.out.println("Sending message to server: " + command.substring(firstSpace));
                        this.client.sendServerMessage(command.substring(firstSpace));
                    }
                    else if (command.startsWith("setusername")) {
                        System.out.println("Setting username to: " + command.substring(firstSpace));
                        this.client.setUsername(command.substring(firstSpace));
                    }
                }
                else {
                    this.out.println(command);
                }
            }
        } catch (IOException e) {
            System.err.println("[CLIENT] ServerOut: " + e.getMessage());
            e.printStackTrace();
        }
        // Close the socket and the input stream
        try {
            this.serverSocket.close();
            this.keyboard.close();
        } catch (IOException e) {
            System.err.println("[CLIENT] Failed to close serverSocket and input stream: " + e.getMessage());
            e.printStackTrace();
        }

    }

    protected void sendToServer(String message) {
        this.out.println(message);
    }
}
