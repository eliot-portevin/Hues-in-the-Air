package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Server implements Runnable {

  private final int PORT;

  private final ArrayList<ClientHandler> clientsHandlers = new ArrayList<>();
  private final ArrayList<Thread> clientThreads = new ArrayList<>();
  private final HashMap<String, Lobby> lobbies = new HashMap<>();

  private ServerSocket listener;

  private boolean shuttingDown = false;

  public Server(String PORT) {
    this.PORT = Integer.parseInt(PORT);
  }

  /**
   * Waits for client connections. When a client wants to connect to the server, the addClient
   * method is called.
   */
  public void run() {
    try {
      this.listener = new ServerSocket(this.PORT);

      while (true) {
        if (!shuttingDown) {
          System.out.println("[SERVER] Waiting for client connection...");
          Socket client = listener.accept();
          this.addClient(client);
        } else {
          break;
        }
      }
    } catch (IOException e) {
      System.err.println("Server exception: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private void addClient(Socket clientSocket) throws IOException {
    ClientHandler clientHandler = new ClientHandler(clientSocket, this);
    this.clientsHandlers.add(clientHandler);

    Thread clientThread = new Thread(clientHandler);
    this.clientThreads.add(clientThread);
    clientThread.start();

    System.out.println("[SERVER] Connected to Client!");
  }

  protected void removeClient(ClientHandler client) {
    client.running = false;
    this.clientThreads.get(this.clientsHandlers.indexOf(client)).interrupt();
    this.clientThreads.remove(this.clientsHandlers.indexOf(client));
    this.clientsHandlers.remove(client);

    System.out.println("[SERVER] Client disconnected!");
  }

  protected void shutdown() throws IOException {
    this.shuttingDown = true;
    this.listener.close();

    for (Thread clientThread : this.clientThreads) {
      clientThread.interrupt();
    }
    for (ClientHandler client : this.clientsHandlers) {
      removeClient(client);
    }

    System.out.println("[SERVER] Server shutdown!");
    System.exit(0);
  }

  protected ArrayList<ClientHandler> getClientHandlers() {
    return this.clientsHandlers;
  }

  protected ClientHandler getClientHandler(String username) {
    for (ClientHandler client : this.clientsHandlers) {
      if (client.getUsername() != null) {
        if (client.getUsername().equals(username)) {
          return client;
        }
      }
    }
    return null;
  }

  protected void createLobby(String lobbyName, String password, ClientHandler client) {
    for (String lobby : this.lobbies.keySet()) {
      if (lobby.equals(lobbyName)) {
        // Lobby already exists
        return;
      }
    }

    this.lobbies.put(lobbyName, new Lobby(lobbyName, password));
    System.out.printf("%s created lobby %s\n", client.getUsername(), lobbyName);
    this.lobbies.get(lobbyName).addClient(client, password);
  }

  protected void joinLobby(String lobbyName, String password, ClientHandler client) {
    for (String lobby : this.lobbies.keySet()) {
      if (lobby.equals(lobbyName)) {
        this.lobbies.get(lobbyName).addClient(client, password);
        return;
      }
    }
    // Lobby does not exist
  }
}
