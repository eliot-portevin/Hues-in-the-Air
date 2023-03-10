package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Handles the input from the server
 */
public class ServerIn implements Runnable {

    private Socket serverSocket;
    private BufferedReader in;

    /**
     * Creates an instance of ServerConnection*/
    public ServerIn(Socket serverSocket) throws IOException {
        this.serverSocket = serverSocket;
        this.in = new BufferedReader(new InputStreamReader(this.serverSocket.getInputStream()));
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
}
