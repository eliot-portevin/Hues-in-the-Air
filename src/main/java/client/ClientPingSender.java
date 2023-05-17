package client;

/** Class that sends pings to the server. */
public class ClientPingSender implements Runnable {

  Client client;
  boolean running = true;

  /**
   * Initialises the ClientPingSender by setting the client
   * @param client The client to send pings to
   */
  public ClientPingSender(Client client) {
    this.client = client;
  }

  /**
   * Method from Runnable interface.
   * Sends a ping to the server every 300ms. If the server doesn't respond to 3 pings, the client
   * logs out.
   */
  public void run() {
    long currentTime = System.currentTimeMillis();

    while (this.running) {
      if (System.currentTimeMillis() - currentTime > 300) {
        currentTime = System.currentTimeMillis();

        if (client.serverHasPonged) {
          client.serverHasPonged = false;
          client.ping();
        } else {
          if (client.noAnswerCounter > 3) {
            System.out.println(
                    "[CLIENT_PING_SENDER] Server didn't respond to 3 pings. Logging out.");
            this.client.exit();
          } else {
            client.noAnswerCounter++;
            client.serverHasPonged = true;
          }
        }
      }
    }
  }
}
