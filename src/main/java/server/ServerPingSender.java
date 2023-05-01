package server;

import java.util.ArrayList;

/**
 * The class which sends a ping to the client every 300ms.
 * If the client doesn't respond to 3 pings,
 * the server removes the client.
 */
public class ServerPingSender implements Runnable {

  /** An ArrayList of the ClientHandlers. */
  private ArrayList<ClientHandler> clients;
  /** Used to know if the client is running. */
  private boolean running = true;
  /** The server of the pingSender. */
  private Server server;
  /**
   * Creates a new ServerPingSender. It sends a ping to the client every 300ms.
   * If the client doesn't respond to 3 pings, the server removes the client.
   * @param clientList The list of clients
   * @param pingServer The server
   */
  public ServerPingSender(final ArrayList<ClientHandler> clientList,
                          final Server pingServer) {
    this.clients = clientList;
    this.server = pingServer;
  }

  /**
   * Method from Runnable interface.
   *
   * Sends a ping to the client every 300ms.
   * If the client doesn't respond to 3 pings, the server
   * removes the client.
   */
  @SuppressWarnings("checkstyle:SimplifyBooleanExpression")
  public void run() {
    while (this.running) {
      try {
        Thread.sleep(300);
        ClientHandler client;

        for (int i = 0; i < this.clients.size(); i++) {
          client = this.clients.get(i);
          if (client.getClientConnected()) {
            client.setClientConnected(false);
            client.ping();
          } else {
            if (client.getNoAnswerCounter() > 3) {
              server.removeClient(client);
            } else {
              client.setNoAnswerCounter(client.getNoAnswerCounter() + 1);
              client.setClientConnected(true);
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
