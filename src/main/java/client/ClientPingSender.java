package client;

public class ClientPingSender implements Runnable {

  Client client;
  boolean running = true;

  /**
   * Initialises the ClientPingSender by setting the client
   * @param client
   */
  public ClientPingSender(Client client) {
    this.client = client;
  }

  /**
   * Method from Runnable interface.
   *
   * Sends a ping to the server every 300ms. If the server doesn't respond to 3 pings, the client
   * logs out.
   */
  public void run() {
    while (this.running) {
      try {
        Thread.sleep(300);
        if (client.connectedToServer) {
          client.connectedToServer = false;
          client.ping();
        } else {
          if (client.noAnswerCounter > 3) {
            System.out.println(
                "[CLIENT_PING_SENDER] Server didn't respond to 3 pings. Logging out.");
            this.client.exit();
          } else {
            client.noAnswerCounter++;
            client.connectedToServer = true;
          }
        }
      } catch (InterruptedException e) {
        System.out.println("CLient ping sender couldn't sleep.");
        throw new RuntimeException(e);
      }
    }
  }
}
