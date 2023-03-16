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

    public void addClient(ClientHandler client, String clientPwd){
        if (clientPwd.equals(this.password)) {
            if (this.getNumPlayers() < 4) {
                this.clients.add(client);
                client.enterLobby(this.name);
            }
        }
    }

    public String getName() {
        return name;
    }

    public boolean clientInLobby(String username){
        for (ClientHandler client : this.clients) {
            if (client.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public int getNumPlayers() {
        return this.clients.size();
    }
}
