package server;

import java.util.ArrayList;

public class Lobby {
  private String name;
  private String password;
  private final ArrayList<ClientHandler> clients = new ArrayList<>();

  public Lobby(String name, String password) {
    this.name = name;
    this.password = password;
  }

  protected void addClient(ClientHandler client, String clientPwd) {
    if (clientPwd.equals(this.password)) {
      if (this.getNumPlayers() < 4) {
        this.clients.add(client);
        client.enterLobby(this);
      }
    }
  }

  protected void removeClient(ClientHandler client) {
    this.clients.remove(client);
  }

  protected String getName() {
    return name;
  }

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
