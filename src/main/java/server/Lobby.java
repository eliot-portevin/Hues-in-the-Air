package server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** The lobby class which handles the logic for a lobby. */
public class Lobby {
  private final String name;
  private final String password;
  private final ArrayList<ClientHandler> clients = new ArrayList<>();

  private final HashMap<ClientHandler, Boolean> clientsReady = new HashMap<>();
  private final HashMap<ClientHandler, Color> clientsAndColours = new HashMap<>();

  private final Logger LOGGER = LogManager.getLogger(getClass());

  Thread gameThread;
  private ServerGame game;
  private boolean isInGame = false;
  int gamesPlayed = 0;

  /**
   * Creates a new lobby.
   *
   * @param name The name of the lobby
   * @param password The password of the lobby
   */
  public Lobby(String name, String password) {
    this.name = name;
    this.password = password;
    Server.getInstance().updateLobbyList();
  }
  /**
   * Adds a client to the lobby.
   *
   * @param client The client to add
   * @param clientPwd The password of the client
   */
  protected void addClient(ClientHandler client, String clientPwd) {
    if (clientPwd.equals(this.password) && !this.isInGame) {
      if (this.getNumPlayers() < 4) {
        synchronized (this.clients) {
          this.clients.add(client);
          this.clientsReady.put(client, false);
          this.clientsAndColours.put(client, this.getFreeColour());
          client.enterLobby(this);
          Server.getInstance().updateLobbyList();
          Server.getInstance().updateClientList();
          LOGGER.info("Client " + client.getUsername() + " joined lobby " + this.name);
        }
      }
    }
  }

  /** Gets an available colour for a new client. */
  private Color getFreeColour() {
    ArrayList<Color> freeColours = new ArrayList<>(ServerGame.blockColours);
    for (Color colour : this.clientsAndColours.values()) {
      freeColours.remove(colour);
    }
    return freeColours.get((int) Math.floor(Math.random() * freeColours.size()));
  }
  /**
   * Removes a client from the lobby.
   *
   * @param client The client to remove
   */
  protected void removeClient(ClientHandler client) {
    if (this.isInGame) {
      this.game.endGame();
      this.isInGame = false;
    }

    synchronized (this.clients) {
      client.exitLobby();
      this.clients.remove(client);
      this.clientsReady.remove(client);
      this.clientsAndColours.remove(client);
      Server.getInstance().updateLobbyList();
      Server.getInstance().updateClientList();
      LOGGER.info("Client " + client.getUsername() + " left lobby " + this.name);
    }
    this.updateLobbyList();
    this.checkReady();
  }

  /**
   * Returns the name of the lobby. Used to add and remove clients of it.
   *
   * @return the name of the lobby
   */
  public String getName() {
    return name;
  }
  /**
   * Checks if a client is in the lobby.
   *
   * @param username The username of the client
   * @return if the client is in the lobby
   */
  protected boolean isInLobby(String username) {
    for (ClientHandler client : this.clients) {
      if (client.getUsername().equals(username)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns the number of players. Used to see whether the lobby is full or not. Used by the
   * server: if the lobby is empty, it is deleted.
   *
   * @return the number of players in the lobby
   */
  protected int getNumPlayers() {
    return this.clients.size();
  }

  /**
   * Returns the name of the clientHandlers in the lobby.
   *
   * @return the name of the clientHandlers in the lobby
   */
  protected ArrayList<ClientHandler> getClientHandlers() {
    return this.clients;
  }

  /** Called when the lobbyList has to be updated */
  public void updateLobbyList() {
    for (ClientHandler client : this.clients) {
      client.listLobby();
    }
  }

  /**
   * Called from {@link ClientHandler} when a client toggles their ready status.
   *
   * @param client the client that toggled their ready status
   * @param isReady the new ready status of the client
   */
  public void toggleClientReady(ClientHandler client, boolean isReady) {
    if (!isInLobby(client.getUsername())) {
      LOGGER.warn(
          "Client "
              + client.getUsername()
              + " tried to toggle their ready status in lobby "
              + this.name
              + " but they are not in the lobby.");
      return;
    }
    // Update the ready status of the client
    this.clientsReady.put(client, isReady);

    this.updateLobbyList();
    this.checkReady();
  }

  /**
   * Iterates through all clients and checks if they are ready. If that is the case, the game
   * starts.
   */
  private void checkReady() {
    for (ClientHandler c : this.clientsReady.keySet()) {
      if (!this.clientsReady.get(c)) {
        return;
      }
    }
    // Upon closing of the lobby, a game would be started with 0 players
    if (this.getNumPlayers() < 1) {
      return;
    }
    this.startGame();
  }

  /** Starts the game. */
  private void startGame() {
    // The game instance starts itself
    String gameId = this.getName();
    this.game = new ServerGame(this.clientsAndColours, gameId, this);

    // Tell the clients to load their game screen
    for (ClientHandler client : this.getClientHandlers()) {
      client.startGame();
    }

    // Starts the game thread
    this.gameThread = new Thread(game);
    gameThread.start();

    Server.getInstance().addGame(this.game);
    this.isInGame = true;
  }

  /**
   * Getter for the game.
   *
   * @return the game
   */
  protected ServerGame getGame() {
    return this.game;
  }

  /**
   * Returns the lobby list as a string containing all the information about the lobby.
   *
   * @return the lobby list as a string
   */
  public String listLobby() {
    String command = ServerProtocol.UPDATE_LOBBY_LIST.toString() + ServerProtocol.SEPARATOR;

    command +=
        this.clients.stream()
            .map(
                c ->
                    c.getUsername()
                        + " "
                        + this.clientsReady.get(c)
                        + " "
                        + this.clientsAndColours.get(c))
            .collect(Collectors.joining(ServerProtocol.SUBSEPARATOR.toString()));
    return command;
  }

  /** Ends the game thread by setting the running status of the game to false. */
  protected void endGame() {
    this.game.running = false;
    this.isInGame = false;
    this.gamesPlayed++;

    try {
      this.gameThread.interrupt();
    } catch (Exception e) {
      LOGGER.warn("Could not interrupt game thread.");
    }

    Server.getInstance().endGame(this.game);
  }
}
