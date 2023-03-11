package server;

import client.ClientProtocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;

public class ClientHandler implements Runnable {

    // The client's socket
    private final Socket client;

    // Input and output streams
    private final BufferedReader in;
    private final PrintWriter out;

    // The server: used to access the list of clients
    private final Server server;

    // Is the client still connected?
    private boolean running = true;

    private String username;

    /**
     * Is in charge of a single client.
     * */
    public ClientHandler(Socket clientSocket, Server server) throws IOException {
        this.client = clientSocket;
        this.in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        this.out = new PrintWriter(client.getOutputStream(), true);
        this.server = server;

        this.getClientUsername();
    }
    /**
     * Handles the client's input.
     * If the client sends "exit", the server shuts down.
     * If the client sends "say", the server broadcasts the message to all clients.
     *
     */
    @Override
    public void run() {
        while (this.running) {
            String[] command = this.receiveFromClient();
            if (command != null) {
                this.protocolSwitch(command);
            }
        }
        try {
            client.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessageServer(String username, String messageContent) {
        String message = ServerProtocol.SEND_MESSAGE_SERVER.toString() + ServerProtocol.SEPARATOR + username + ServerProtocol.SEPARATOR + messageContent;
        for (ClientHandler client : this.server.getClientHandlers()) {
            client.out.println(message);
        }
    }

    private String[] receiveFromClient() {
        try {
            return this.in.readLine().split(ServerProtocol.SEPARATOR.toString());
        } catch (IOException e) {
            System.err.println("[CLIENT_HANDLER] " + this.username + " failed to receive message from client");
            System.out.println(Arrays.toString(e.getStackTrace()));
            return null;
        }
    }

    private void protocolSwitch(String[] command) {
        ClientProtocol protocol = ClientProtocol.valueOf(command[0]);

        switch (protocol) {
            case SET_USERNAME -> this.username = command[1];

            case SEND_MESSAGE_SERVER -> this.sendMessageServer(command[1], command[2]);
        }
    }

    private void getClientUsername() {
        this.out.println(ServerProtocol.REQUEST_USERNAME);
    }
}