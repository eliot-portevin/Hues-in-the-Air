package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The server class. This class is responsible for managing the server.
 * It contains a list of all clients, a list of all lobbies and a list
 * of all games. It also contains a ServerSocket which is
 * used to listen for client connections.
 */
public class Server implements Runnable {

  /** the port of the server. */
  private final int port;

  /** An ArrayList for all clientHandlers. */
  private final ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
  /** An ArrayList for the Threads of the clients. */
  private final ArrayList<Thread> clientThreads = new ArrayList<>();
  /** A HashMap for all lobbies with their names. */
  private final HashMap<String, Lobby> lobbies = new HashMap<>();
  /** A Map for the ServerGame used for the game state. */
  private Map<ServerGame, Map.Entry<Integer, Boolean>> games
          = new LinkedHashMap<>();

  /** The listener of the ServerSocket. */
  private ServerSocket listener;
  /** Used to try to connect the client to the server. */
  private boolean shuttingDown = false;
  /** Used to know if clients are connected to the server. */
  private boolean noClientConnected = true;


  /** The instance of the server. */
  private static Server instance;
  /** The logger for the server. */
  private final Logger logger;

  /** The maximum amount of characters a username can have. */
  public static final int MAX_NAME_LENGTH = 24;

  /**
   * Creates a new server.
   *
   * @param serverPort The port which the server will listen on
   */
  public Server(final int serverPort) {
    this.port = serverPort;
    this.logger = LogManager.getLogger(Server.class);
    instance = this;
  }

  /**
   * Waits for client connections.
   * When a client wants to connect to the server, the addClient
   * method is called.
   */
  public void run() {
    try {
      this.listener = new ServerSocket(this.port);
      Thread pingSender
              = new Thread(new ServerPingSender(this.clientHandlers, this));

      while (true) {
        if (!shuttingDown) {
          if (this.noClientConnected) {
            this.noClientConnected = false;
            pingSender.start();
          }
          try {
            Socket client = listener.accept();
            this.addClient(client);
            // LOGGER.info("Client was added");
          } catch (SocketException e) {
            if (this.shuttingDown) {
              logger.error("Unable to connect with message: " + e.getMessage());
              break;
            }
          }
        } else {
          break;
        }
      }
    } catch (IOException e) {
      logger.error("Server exception: " + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * Adds a client to the list of clients
   * and starts a dedicated thread for the client.
   *
   * @param clientSocket The socket of the client
   * @throws IOException If the client socket is closed
   */
  private void addClient(final Socket clientSocket) throws IOException {
    ClientHandler clientHandler = new ClientHandler(clientSocket, this);
    this.clientHandlers.add(clientHandler);

    Thread clientThread = new Thread(clientHandler);
    this.clientThreads.add(clientThread);
    clientThread.start();

    logger.info("[Server] Connected to Client!");
    this.updateLobbyList();
    this.updateClientList();
  }

  /**
   * Called from {@link ClientHandler} when a client disconnects ({@link
   * client.ClientProtocol#EXIT}). Removes the client from the list of clients,
   * from its lobby and interrupts the client's dedicated thread.
   *
   * @param client The client that disconnected
   */
  protected void removeClient(final ClientHandler client) {
    client.setRunning(false);
    Optional<Lobby> lobby = Optional.ofNullable(client.getLobby());
    lobby.ifPresent(value -> value.removeClient(client));
    this.clientThreads.get(this.clientHandlers.indexOf(client)).interrupt();
    this.clientThreads.remove(this.clientHandlers.indexOf(client));
    this.clientHandlers.remove(client);

    logger.info("Client " + client.getUsername() + " disconnected");
    this.updateLobbyList();
    this.updateClientList();
  }

  /**
   * Called when a lobby is empty and should be removed.
   *
   * @param lobby The lobby that should be removed
   */
  protected void removeLobby(final Lobby lobby) {
    this.lobbies.remove(lobby.getName());
    this.updateLobbyList();
    logger.info("The lobby " + lobby.getName()
            + " was removed because it was empty.");
  }

  /**
   * Called from {@link ClientHandler} when a client disconnects.
   *
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

    logger.info("Server shutting down.");
    System.exit(0);
  }

  /**
   * Used by {@link ClientHandler} to list the clientHandlers.
   *
   * @return The list of clientHandlers
   */
  protected ArrayList<ClientHandler> getClientHandlers() {
    return this.clientHandlers;
  }

  /**
   * @return The games that have been played,
   * or are still playing, and their status.
   */
  protected Map<ServerGame, AbstractMap.Entry<Integer, Boolean>> getGames() {
    return this.games;
  }

  /** Sorts the games by the number of levels completed. */
  private void sortGames() {
    List<Map.Entry<ServerGame, Map.Entry<Integer, Boolean>>> list =
        new LinkedList<>(this.games.entrySet());

    list.sort(((o1, o2)
            -> o2.getValue().getKey().compareTo(o1.getValue().getKey())));

    this.games =
        list.stream()
            .collect(
                Collectors.toMap(
                    Map.Entry::getKey, Map.Entry::getValue,
                        (a, b) -> b, LinkedHashMap::new));
  }

  /**
   * Returns the name of the client for given username.
   *
   * @param username of the client
   * @return The name of the client
   */
  protected ClientHandler getClientHandler(final String username) {
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
   * Called from {@link ClientHandler} when a client wants to create a lobby.
   * Creates a new lobby and adds the client to it.
   *
   * @param lobbyName The name of the lobby
   * @param password The password of the lobby
   * @param client The client that wants to create the lobby
   */
  protected void createLobby(final String lobbyName, final String password,
                             final ClientHandler client) {
    for (String lobby : this.lobbies.keySet()) {
      if (lobby.equals(lobbyName)) {
        // Lobby already exists
        logger.warn("Lobby does already exist and cannot be created.");
        return;
      }
    }

    this.lobbies.put(lobbyName, new Lobby(lobbyName, password));
    logger.info(client.getUsername() + " created lobby " + lobbyName + ".");
    this.lobbies.get(lobbyName).addClient(client, password);
  }

  /**
   * Called from {@link ClientHandler} when a client wants to join a lobby
   * Adds the client to the
   * lobby.
   *
   * @param lobbyName The name of the lobby
   * @param password The password of the lobby
   * @param client The client that wants to join the lobby
   */
  protected void joinLobby(final String lobbyName, final String password,
                           final ClientHandler client) {
    for (String lobby : this.lobbies.keySet()) {
      if (lobby.equals(lobbyName)) {
        this.lobbies.get(lobbyName).addClient(client, password);
        this.lobbies.get(lobbyName).updateLobbyList();
        return;
      }
    }
  }

  /**
   * Sends a message to all clients in the server
   * containing the list of all lobbies and their clients.
   */
  protected void updateLobbyList() {
    synchronized (this.lobbies) {
      for (Lobby lobby : this.lobbies.values()) {
        if (lobby.getNumPlayers() == 0) {
          this.removeLobby(lobby);
        }
      }
      for (ClientHandler client : this.clientHandlers) {
        client.updateLobbyList();
      }
    }
  }

  /**
   * Sends a message to all clients in the server
   * containing the list of all clients.
   */
  protected void updateClientList() {
    synchronized (this.clientHandlers) {
      for (ClientHandler client : this.clientHandlers) {
        client.updateClientList();
      }
    }
  }

  /**
   * Sends a message to all clients in the server containing
   * the list of all games and their status (running or finished).
   */
  private void updateGameList() {
    this.sortGames();

    synchronized (this.clientHandlers) {
      for (ClientHandler client : this.clientHandlers) {
        client.updateGameList();
      }
    }
  }

  /**
   * Produces an array of all lobbies and their clients.
   * Called from {@link ClientHandler} each time
   * the client list is updated. This list is then sent to the client.
   *
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

  /**
   * Called from {@link ClientHandler} for the logger and from {@link Lobby}
   * to update the lists.
   *
   * @return the instance of the server
   */
  public static Server getInstance() {
    return instance;
  }

  /**
   * Called from {@link Lobby} when a game has been started.
   * Adds the game to the list of games and sends a message to all clients
   * in the server containing the list of all games. The info stored
   * in the map is: game instance, levels completed,
   * whether the game is running or not.
   *
   * @param game The game instance that has been started
   */
  protected void addGame(final ServerGame game) {
    this.games.put(game,
            new AbstractMap.SimpleEntry<>(game.getLevelsCompleted(), true));
    this.updateGameList();
  }

  /**
   * Called from {@link ServerGame} when a game has ended.
   * Sets the game to finished and sends a message to all clients in the server
   * containing the list of all games. The info stored in the map is:
   * game instance, levels completed, whether the game is running or not.
   *
   * @param game The game instance that has ended
   */
  protected void endGame(final ServerGame game) {
    this.games.put(game,
            new AbstractMap.SimpleEntry<>(game.getLevelsCompleted(), false));

    for (ClientHandler client : game.getPlayers()) {
      client.gameEnded();
    }

    this.updateGameList();
  }

  /**
   * Called from {@link ServerGame#nextLevel()} when a level has been completed.
   * Updates the number of levels completed and sends a message to all clients
   * in the server containing the list of all games.
   * The info stored in the map is: game instance, levels completed,
   * whether the game is running or not.
   *
   * @param game The game instance that has completed a level
   */
  protected void updateGameLevelsCompleted(final ServerGame game) {
    this.games.put(game,
            new AbstractMap.SimpleEntry<>(game.getLevelsCompleted(), true));
    this.updateGameList();
  }
}
