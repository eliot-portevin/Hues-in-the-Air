package server;

import java.util.ArrayList;
import java.util.logging.Logger;

public class Lobby {
  private final String name;
  private final String password;
  private final ArrayList<ClientHandler> clients = new ArrayList<>();

  private final Logger LOGGER = Logger.getLogger(Lobby.class.getName());
  /**
   * Creates a new lobby.
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
   * @param client The client to add
   * @param clientPwd The password of the client
   */
  protected void addClient(ClientHandler client, String clientPwd) {
    if (clientPwd.equals(this.password)) {
      if (this.getNumPlayers() < 4) {
        this.clients.add(client);
        client.enterLobby(this);
        Server.getInstance().updateLobbyList();
        Server.getInstance().updateClientList();
        LOGGER.info("Client " + client.getUsername() + " joined lobby " + this.name);
      }
    }
  }
  /**
   * Removes a client from the lobby.
   * @param client The client to remove
   */
  protected void removeClient(ClientHandler client) {
    client.exitLobby();
    this.clients.remove(client);
    Server.getInstance().updateLobbyList();
    Server.getInstance().updateClientList();
    LOGGER.info("Client " + client.getUsername() + " left lobby " + this.name);

    this.updateLobbyList();
  }

  /**
   * Returns the name of the lobby. Used to add and remove clients of it.
   * @return
   */
  public String getName() {
    return name;
  }
  /**
   * Checks if a client is in the lobby.
   * @param username The username of the client
   * @return if the client is in the lobby
   */
  protected boolean clientInLobby(String username) {
    for (ClientHandler client : this.clients) {
      if (client.getUsername().equals(username)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns the number of players. Used to see whether the lobby is full or not.
   * Used by the server:if the lobby is empty, it is deleted.
   * @return
   */
  protected int getNumPlayers() {
    return this.clients.size();
  }

  /**
   * Returns the name of the clientHandlers in the lobby
   * @return
   */
  protected ArrayList<ClientHandler> getClientHandlers() {
    return this.clients;
  }

  /**
   * Called when the lobbyList has to be updated
   */
  public void updateLobbyList() {
    for (ClientHandler client : this.clients) {
      client.listLobby();
    }
  }
}