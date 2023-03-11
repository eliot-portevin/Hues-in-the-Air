package client;

import server.ServerProtocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Handles the input from the server
 */
public class ServerIn implements Runnable {

    private final Socket serverSocket;
    private final BufferedReader in;
    private final Client client;

    private Boolean running = true;

    /**
     * Creates an instance of ServerConnection*/
    public ServerIn(Socket serverSocket, Client client) throws IOException {
        this.serverSocket = serverSocket;
        this.in = new BufferedReader(new InputStreamReader(this.serverSocket.getInputStream()));
        this.client = client;
    }

    /**
     * From the Runnable interface. Runs the ServerIn thread
     * to receive commands from the server
     */
    @Override
    public void run() {
        while(running) {
            String[] command = this.receiveFromServer();
            protocolSwitch(command);
        }
        try {
            this.serverSocket.close();
            this.in.close();
        } catch (IOException e) {
            System.err.println("[CLIENT] Failed to close serverSocket and input stream: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String[] receiveFromServer() {
        try {
            return this.in.readLine().split(ServerProtocol.SEPARATOR.toString());
        } catch (IOException e) {
            System.err.println("[CLIENT] failed to receive message from server: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private void protocolSwitch(String[] command) {
        ServerProtocol protocol = ServerProtocol.valueOf(command[0]);

        switch (protocol) {
            case SEND_MESSAGE_SERVER:
                System.out.println("[" + command[1] + "] " + command[2]);

            case REQUEST_USERNAME:
                this.client.sendUsername();
        }
    }
}
