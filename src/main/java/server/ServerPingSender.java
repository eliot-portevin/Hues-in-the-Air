package server;

import java.util.ArrayList;

public class ServerPingSender implements Runnable {

  ArrayList<ClientHandler> clients;
  boolean running = true;
  private Server server;

  public ServerPingSender(ArrayList<ClientHandler> clients, Server server) {
    this.clients = clients;
    this.server = server;
  }

  /**
   * Method from Runnable interface.
   *
   * Sends a ping to the client every 300ms. If the client doesn't respond to 3 pings, the server
   * removes the client.
   */
  public void run() {
    while (this.running) {
      try {
        Thread.sleep(300);
        ClientHandler client;

        for (int i = 0; i < this.clients.size(); i++) {
          client = this.clients.get(i);
          if (client.clientConnected) {
            client.clientConnected = false;
            client.ping();
          } else {
            if (client.noAnswerCounter > 3) {
              server.removeClient(client);
            } else {
              client.noAnswerCounter++;
              client.clientConnected = true;
            }
          }
        }
      } catch (InterruptedException e) {
        System.out.println("Server ping sender couldn't sleep");
        throw new RuntimeException(e);
      }
    }
  }
}