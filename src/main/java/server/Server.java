package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Server implements Runnable {

  private final int PORT;

  private final ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
  private final ArrayList<Thread> clientThreads = new ArrayList<>();
  private final HashMap<String, Lobby> lobbies = new HashMap<>();

  private ServerSocket listener;

  private boolean shuttingDown = false;
  private boolean noClientConnected = true;

  private static Server instance;
  private final Logger LOGGER;

  public Server(int PORT) {
    this.PORT = PORT;
    this.LOGGER = LogManager.getLogger(Server.class);
    instance = this;
  }

  /**
   * Waits for client connections. When a client wants to connect to the server, the addClient
   * method is called.
   */
  public void run() {
    try {
      this.listener = new ServerSocket(this.PORT);
      Thread pingSender = new Thread(new ServerPingSender(this.clientHandlers, this));

      while (true) {
        if (!shuttingDown) {
          if(this.noClientConnected) {
            this.noClientConnected = false;
            pingSender.start();
          }
          LOGGER.info("Waiting for client connection...");
          try {
            Socket client = listener.accept();
            this.addClient(client);
           // LOGGER.info("Client was added");
          }
          catch (SocketException e) {
            if (this.shuttingDown) {
              LOGGER.error("Unable to connect with message: " + e.getMessage());
              break;
            }
          }
        } else {
          break;
        }
      }
    } catch (IOException e) {
      LOGGER.error("Server exception: " + e.getMessage());
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

    LOGGER.info("[Server] Connected to Client!");
    this.updateLobbyList();
    this.updateClientList();
  }

  /**
   * Called from {@link ClientHandler} when a client disconnects ({@link
   * client.ClientProtocol#EXIT}). Removes the client from the list of clients, from its lobby and
   * interrupts the client's dedicated thread.
   */
  protected void removeClient(ClientHandler client) {
    client.running = false;
    Optional<Lobby> lobby = Optional.ofNullable(client.getLobby());
    if (lobby.isPresent()) {
      client.getLobby().removeClient(client);
    }
    this.clientThreads.get(this.clientHandlers.indexOf(client)).interrupt();
    this.clientThreads.remove(this.clientHandlers.indexOf(client));
    this.clientHandlers.remove(client);

    LOGGER.info("Client " + client.getUsername() + " disconnected");
    this.updateLobbyList();
    this.updateClientList();
  }

  /**
   * Called when a lobby is empty and should be removed.
   * @param lobby The lobby that should be removed
   */
  protected void removeLobby(Lobby lobby) {
    this.lobbies.remove(lobby.getName());
    this.updateLobbyList();
    LOGGER.info("Lobby " + lobby + " removed");
  }

  /**
   * Called from {@link ClientHandler} when a client disconnects
   * @throws IOException If the socket fails to close
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

    LOGGER.info("Server shutting down.");
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
   */
  protected void createLobby(String lobbyName, String password, ClientHandler client) {
    for (String lobby : this.lobbies.keySet()) {
      if (lobby.equals(lobbyName)) {
        // Lobby already exists
        LOGGER.warn("Lobby does already exist and cannot be created.");
        return;
      }
    }

    this.lobbies.put(lobbyName, new Lobby(lobbyName, password));
    LOGGER.info(client.getUsername() + " created lobby " + lobbyName + "\n");
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

  /**
   * Sends a message to all clients in the server containing the list of all lobbies and their
   * clients.
   */
  protected void updateLobbyList() {
    for (Lobby lobby : this.lobbies.values()) {
      if (lobby.getNumPlayers() == 0) {
        this.removeLobby(lobby);
        LOGGER.info("Lobby " + lobby + " removed.");
      }
    }
    for (ClientHandler client : this.clientHandlers) {
      client.updateLobbyList();
    }
  }

  /**
   * Sends a message to all clients in the server containing the list of all clients.
   */
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
