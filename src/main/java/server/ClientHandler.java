package server;

import client.ClientProtocol;
import static shared.Encryption.*;

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

    protected boolean running = true;

    // Client values
    private String username;
    private int missedConnections = 0;

    /**
     * Is in charge of a single client.
     * */
    public ClientHandler(Socket clientSocket, Server server) throws IOException {
        this.client = clientSocket;
        this.in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        this.out = new PrintWriter(client.getOutputStream(), true);
        this.server = server;

        this.requestUsernameFromClient();
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
            // Receive message, decrypt it and split it into an array
            String message = this.receiveFromClient();
            if (message == null) {
                this.missedConnections++;
                System.out.println("[CLIENT_HANDLER] " + this.username + " failed to receive message from client");
            } else {
                this.missedConnections = 0;
                String[] command = decrypt(message).split(ServerProtocol.SEPARATOR.toString());
                this.protocolSwitch(command);
            }
            if (this.missedConnections >= 3) {
                this.running = false;
            }
        }
        try {
            client.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * The client linked to this ClientHandler wants to send a message to all clients on the server.
     * <p>
     *     See {@link ServerProtocol#SEND_MESSAGE_SERVER}
     * </p>
     * */
    private void sendMessageServer(String message) {
        String output = ServerProtocol.SEND_MESSAGE_SERVER.toString() + ServerProtocol.SEPARATOR + this.username +
                ServerProtocol.SEPARATOR + message;

        output = encrypt(output);

        for (ClientHandler client : this.server.getClientHandlers()) {
            client.out.println(output);
        }
    }

    /**
     * The client linked to this ClientHandler wants to send a message to another client on the server.
     * <p>
     *     See {@link ServerProtocol#SEND_MESSAGE_CLIENT}
     * </p>
     * */
    private void sendMessageClient(String recipient, String messageContent) {
        String message = ServerProtocol.SEND_MESSAGE_CLIENT.toString() + ServerProtocol.SEPARATOR + this.username +
                ServerProtocol.SEPARATOR + messageContent;

        ClientHandler recipientHandler = this.server.getClientHandler(recipient);
        if (recipientHandler != null) {
            message = encrypt(message);
            this.server.getClientHandler(recipient).out.println(message);
            this.out.println(message);
        }
        else {
            this.out.println(encrypt(ServerProtocol.NO_USER_FOUND.toString()));
        }
    }

    /**
     * Receives commands from the client.
     * */
    private String receiveFromClient() {
        try {
            //TODO Add Logger
            return this.in.readLine();
        } catch (IOException e) {
            System.err.println("[CLIENT_HANDLER] " + this.username + " failed to receive message from client");
            System.out.println(Arrays.toString(e.getStackTrace()));
            return null;
        }
    }

    /**
     * Called from {@link #receiveFromClient()}.
     * <p>
     *     Goes over the different commands of {@link ClientProtocol} and calls the appropriate method.
     * </p>
     * */
    private void protocolSwitch(String[] command) {
        ClientProtocol protocol = ClientProtocol.valueOf(command[0]);

        switch (protocol) {
            case LOGOUT -> {
                this.server.removeClient(this);
            }
            case SET_USERNAME -> {
                System.out.println("[CLIENT_HANDLER] " + this.username + " has set their username to " + command[1]);
                this.username = command[1];
            }
            case SEND_MESSAGE_SERVER -> {
                this.sendMessageServer(command[1]);
            }
            case SEND_MESSAGE_CLIENT -> {
                this.sendMessageClient(command[1], command[2]);
            }
        }
    }

    /**
     * Requests the client's username upon connection.
     * */
    private void requestUsernameFromClient() {
        // TODO Add Logger
        String message = encrypt(ServerProtocol.NO_USERNAME_SET.toString());
        this.out.println(message);
    }

    public String getUsername() {
        return this.username;
    }
}