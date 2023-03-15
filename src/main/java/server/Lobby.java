package server;

import client.Client;

import java.util.ArrayList;

public class Lobby {
    private String name;
    private String password;
    private ArrayList<ClientHandler> clients;


    public Lobby(String name, String password) {
        this.name = name;
        this.password = password;
    }

    /** Another Client joins the game with the code
     */
    public void joinLobby(ClientHandler client, String clientPwd){
        if (clientPwd.equals(this.password)) {
            if (this.getNumPlayers() < 4) {
                this.addPlayer(client);
            }
        }
    }

    private void addPlayer(ClientHandler client) {
        //
    }


    /** Getter for the Lobbyname
     *
     * @return
     */
    public String getLobbyName() {
        return name;
    }

    /** return if the player is in the game
     *
     */
    public boolean isInLobby(String username){
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
