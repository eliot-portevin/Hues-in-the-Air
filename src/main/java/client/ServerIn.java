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
        try {
            while(true) {
                String serverResponse = in.readLine();
                System.out.println("\n[SERVER] " + serverResponse);
            }
        } catch (IOException e) {
            System.err.println("\n[CLIENT] ServerIn: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void protocolSwitch(String[] serverMessage) {
        ServerProtocol protocol = ServerProtocol.valueOf(serverMessage[0]);
        switch (protocol) {
            case REQUEST_USERNAME:
                this.client.sendUsername();
        }
    }
}
