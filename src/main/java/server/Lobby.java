package server;

import java.util.ArrayList;
import java.util.logging.Logger;

public class Lobby {
  private String name;
  private String password;
  private final ArrayList<ClientHandler> clients = new ArrayList<>();

  private static final Logger LOGGER = Logger.getLogger(Lobby.class.getName());
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
    System.out.println("Client " + client.getUsername() + " left lobby!");
    this.clients.remove(client);
    Server.getInstance().updateLobbyList();
    Server.getInstance().updateClientList();
    LOGGER.info("Client " + client.getUsername() + " left lobby " + this.name);
  }

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

  protected int getNumPlayers() {
    return this.clients.size();
  }

  protected ArrayList<ClientHandler> getClientHandlers() {
    return this.clients;
  }
}