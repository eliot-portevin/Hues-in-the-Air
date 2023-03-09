package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server implements Runnable {

    private final int PORT = 9090;
    private final String[] names = {"John", "Paul", "George", "Ringo"};
    private final String[] adjectives = {"the smart", "the funny", "the handsome", "the ugly"};

    private final ArrayList<ClientHandler> clients = new ArrayList<>();
    private final ArrayList<Thread> clientThreads = new ArrayList<>();

    private ServerSocket listener;

    private boolean shuttingDown = false;


    public void run() {
        try {
            this.listener = new ServerSocket(PORT);

            while (true) {
                if (!shuttingDown) {
                    System.out.println("[SERVER] Waiting for client connection...");
                    Socket client = listener.accept();
                    this.addClient(client);
                }
                else {
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }

    }

    public void addClient(Socket clientSocket) throws IOException {
        ClientHandler clientHandler = new ClientHandler(clientSocket, this);
        this.clients.add(clientHandler);

        Thread clientThread = new Thread(clientHandler);
        this.clientThreads.add(clientThread);
        clientThread.start();

        System.out.println("[SERVER] Connected to Client!");
    }

    public void removeClient(ClientHandler client) {
        this.clientThreads.get(this.clients.indexOf(client)).interrupt();
        this.clientThreads.remove(this.clients.indexOf(client));
        this.clients.remove(client);

        System.out.println("[SERVER] Client disconnected!");
    }

    void shutdown() throws IOException {
        this.shuttingDown = true;
        this.listener.close();

        for (Thread clientThread : this.clientThreads) {
            clientThread.interrupt();
        }
        for (ClientHandler client : this.clients) {
            removeClient(client);
        }

        System.out.println("[SERVER] Server shutdown!");
        System.exit(0);
    }

    public String getRandomName() {
        String name = this.names[(int) (Math.random() * this.names.length)];
        String adjective = this.adjectives[(int) (Math.random() * this.adjectives.length)];

        return name + " " + adjective;
    }

    public ArrayList<ClientHandler> getClients() {
        return this.clients;
    }
}