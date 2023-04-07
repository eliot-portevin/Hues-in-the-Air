package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;

public class Server implements Runnable {

  private final int PORT;

  private final ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
  private final ArrayList<Thread> clientThreads = new ArrayList<>();
  private final HashMap<String, Lobby> lobbies = new HashMap<>();

  private ServerSocket listener;

  private boolean shuttingDown = false;
  private boolean noClientConnected = true;
  private Thread pingSender;

  private static Server instance;

  public Server(int PORT) {
    this.PORT = PORT;
    instance = this;
  }

  /**
   * Waits for client connections. When a client wants to connect to the server, the addClient
   * method is called.
   */
  public void run() {
    try {
      this.listener = new ServerSocket(this.PORT);
      this.pingSender = new Thread(new ServerPingSender(this.clientHandlers, this));

      while (true) {
        if (!shuttingDown) {
          if(this.noClientConnected) {
            this.noClientConnected = false;
            this.pingSender.start();
          }
          System.out.println("[SERVER] Waiting for client connection...");
          try {
            Socket client = listener.accept();
            this.addClient(client);
          }
          catch (SocketException e) {
            if (this.shuttingDown) {
              break;
            }
          }
        } else {
          break;
        }
      }
    } catch (IOException e) {
      System.err.println("Server exception: " + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * Adds a client to the list of clients and starts a dedicated thread for the client.
   * @param clientSocket The socket of the client
   * @throws IOException If the client socket is closed
   */
  private void addClient(Socket clientSocket) throws IOException {
    ClientHandler clientHandler = new ClientHandler(clientSocket, this);
    this.clientHandlers.add(clientHandler);

    Thread clientThread = new Thread(clientHandler);
    this.clientThreads.add(clientThread);
    clientThread.start();

    System.out.println("[SERVER] Connected to Client!");
    this.updateLobbyList();
  }

  /**
   * Called from {@link ClientHandler} when a client disconnects ({@link
   * client.ClientProtocol#EXIT}). Removes the client from the list of clients, from its lobby and
   * interrupts the client's dedicated thread.
   */
  protected void removeClient(ClientHandler client) {
    client.running = false;
    if (client.getLobby() != null) {
      client.getLobby().removeClient(client);
    }
    this.clientThreads.get(this.clientHandlers.indexOf(client)).interrupt();
    this.clientThreads.remove(this.clientHandlers.indexOf(client));
    this.clientHandlers.remove(client);

    System.out.println("[SERVER] Client " + client.getUsername() + " disconnected!");
    this.updateLobbyList();
  }

  /**
   * Called from {@link ClientHandler} when a client disconnects
   * @throws IOException
   */
  protected void shutdown() throws IOException {
    this.shuttingDown = true;
    this.listener.close();

    for (Thread clientThread : this.clientThreads) {
      clientThread.interrupt();
    }
    for (ClientHandler client : this.clientHandlers) {
      removeClient(client);
    }

    System.out.println("[SERVER] Server shutdown!");
    System.exit(0);
  }

  protected ArrayList<ClientHandler> getClientHandlers() {
    return this.clientHandlers;
  }

  protected ClientHandler getClientHandler(String username) {
    for (ClientHandler client : this.clientHandlers) {
      if (client.getUsername() != null) {
        if (client.getUsername().equals(username)) {
          return client;
        }
      }
    }
    return null;
  }

  /**
   * Called from {@link ClientHandler} when a client wants to create a lobby
   * Creates a new lobby and adds the client to it.
   * @param lobbyName The name of the lobby
   * @param password The password of the lobby
   * @param client The client that wants to create the lobby
   * @return if the lobby already exists exit early
   */
  protected void createLobby(String lobbyName, String password, ClientHandler client) {
    for (String lobby : this.lobbies.keySet()) {
      if (lobby.equals(lobbyName)) {
        // Lobby already exists
        return;
      }
    }

    this.lobbies.put(lobbyName, new Lobby(lobbyName, password));
    System.out.printf("[SERVER] %s created lobby %s\n", client.getUsername(), lobbyName);
    this.lobbies.get(lobbyName).addClient(client, password);
  }

  /**
   * Called from {@link ClientHandler} when a client wants to join a lobby
   * Adds the client to the lobby.
   * @param lobbyName The name of the lobby
   * @param password The password of the lobby
   * @param client The client that wants to join the lobby
   */
  protected void joinLobby(String lobbyName, String password, ClientHandler client) {
    for (String lobby : this.lobbies.keySet()) {
      if (lobby.equals(lobbyName)) {
        this.lobbies.get(lobbyName).addClient(client, password);
        return;
      }
    }
  }

  protected void updateLobbyList() {
    for (ClientHandler client : this.clientHandlers) {
      client.updateLobbyList();
    }
  }

  protected void updateClientList() {
    for (ClientHandler client : this.clientHandlers) {
      client.updateClientList();
    }
  }

  /**
   * Produces an array of all lobbies and their clients. Called from {@link ClientHandler} each time
   * the client list is updated. This list is then sent to the client.
   * @return An array of all lobbies and their clients
   */
  protected String[][] listLobbies() {
    ArrayList<String[]> lobbyInfos = new ArrayList<>();

    for (Lobby lobby : this.lobbies.values()) {
      ArrayList<String> lobbyInfo = new ArrayList<>();
      lobbyInfo.add(lobby.getName());
      for (ClientHandler client : lobby.getClientHandlers()) {
        lobbyInfo.add(client.getUsername());
      }
      lobbyInfos.add(lobbyInfo.toArray(new String[0]));
    }
    return lobbyInfos.toArray(new String[0][0]);
  }

  public static Server getInstance() {
    return instance;
  }
}
