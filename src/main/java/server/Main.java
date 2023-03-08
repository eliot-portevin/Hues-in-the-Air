package server;

import java.net.*;
import java.io.*;
import java.util.*;

public class Main {

    // Define port number
    private int portNumber;

    // Keep track of all clients
    private ArrayList<ClientHandler> clients;

    // Constructor
    public Main(int portNumber) {
        this.portNumber = portNumber;
        this.clients = new ArrayList<>();
    }

    // Method to start the server
    public void start() {
        // Create a server socket on the specified port
        ServerSocket serverSocket = new ServerSocket(portNumber);
        System.out.println("Server started on port " + portNumber);

        // Continuously accept new clients
        while (true) {
            // Wait for a client to connect
            Socket clientSocket = serverSocket.accept();

            // Create a new thread to handle the client
            ClientHandler client = new ClientHandler(clientSocket, this);

            // Add the client to the list of clients
            clients.add(client);

            // Start the new thread
            client.start();

            // Log the new connection
            System.out.println("New client connected: " + clientSocket.getInetAddress());
        }
    }

    // Method to remove a client from the list
    public void removeClient(ClientHandler client) {
        clients.remove(client);
    }

    public static void main(String[] args) {
        // TODO lots of stuff
        System.out.println("Hello world");
    }

}
