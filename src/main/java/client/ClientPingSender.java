package client;

public class ClientPingSender implements Runnable {

  Client client;
  boolean running = true;

  public ClientPingSender(Client client) {
    this.client = client;
  }

  public void run() {
    while (this.running) {
      try {
        Thread.sleep(300);
        if (client.connectedToServer) {
          client.connectedToServer = false;
          client.ping();
        } else {
          if (client.noAnswerCounter > 3) {
            System.out.println("[CLIENT_PING_SENDER] Server didn't respond to 3 pings. Logging out.");
            this.client.logout();
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
