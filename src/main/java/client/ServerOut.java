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
            while(running) {
                System.out.print("> ");
                String command = this.keyboard.readLine();

                if (command.equals("quit")) {
                    this.out.println(command);
                    this.serverSocket.close();
                    break;
                }
                else if (command.startsWith("say")) {
                    sendServerMessage(command.substring(4));
                }
                else {
                    this.out.println(command);
                }
            }
        } catch (IOException e) {
            System.err.println("[CLIENT] ServerOut: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendServerMessage(String message) {
        String command = ServerProtocol.SEND_MESSAGE_SERVER.toString() + ServerProtocol.SEPARATOR + this.client.getUsername() + ServerProtocol.SEPARATOR + message;
        this.sendToServer(command);
    }

    protected void sendToServer(String message) {
        this.out.println(message);
    }
}
