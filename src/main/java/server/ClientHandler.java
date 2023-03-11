package server;

import client.ClientProtocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;

public class ClientHandler implements Runnable {

    private final Socket client;
    private final BufferedReader in;
    private final PrintWriter out;
    private final Server server;

    private String username;

    /**
     * Is in charge of a single client.
     * */
    public ClientHandler(Socket clientSocket, Server server) throws IOException {
        this.client = clientSocket;
        this.in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        this.out = new PrintWriter(client.getOutputStream(), true);
        this.server = server;
    }
    /**
     * Handles the client's input.
     * If the client sends "exit", the server shuts down.
     * If the client sends "say", the server broadcasts the message to all clients.
     *
     */
    @Override
    public void run() {
        try {
            while (true) {
                String request = in.readLine();
                if (request.equals("exit")) {
                    this.server.shutdown();
                    break;
                }
                else if (request.startsWith("say")) {
                    int firstSpace = request.indexOf(" ");
                    if (firstSpace != -1) {
                        String message = request.substring(firstSpace + 1);
                        this.broadcast(message);
                    }
                }
            }
        } catch(IOException e) {
            System.err.println("IO exception in client handler");
            System.err.println(Arrays.toString(e.getStackTrace()));
        } finally {
            try {
                client.close();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void broadcast(String message) {
        for (ClientHandler client : this.server.getClientHandlers()) {
            if (client != this) {
                client.out.println(message);
            }
        }
    }
}