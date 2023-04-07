package server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Scanner;

/** The main class of the server. It creates a new server and starts it. */
public class ServerMain {
  private static final Logger LOGGER = LogManager.getLogger(ServerMain.class);

  /**
   * Starts the server. If the port number is invalid, the default port 9090 is used.
   *
   * @param args The port number of the server.
   */
  public static void main(String[] args) {
    int PORT = 9090;

    try {
      LOGGER.info("Trying to start server on port " + args[0]);
      System.out.println("Trying to start server on port " + args[0]);
      PORT = Integer.parseInt(args[0]);
      if (PORT < 1024 || PORT > 65535) {
        PORT = 9090;
        LOGGER.warn("Port number must be between 1024 and 65535. Using default port " + PORT);
        System.out.println("Port number must be between 1024 and 65535. Using default port " + PORT);
      }
    } catch (NumberFormatException e) {
      LOGGER.warn("Port number must be an integer. Using default port 9090.");
      System.out.println("Port number must be an integer. Using default port 9090.");
    }

    Server server = new Server(PORT);
    Thread serverThread = new Thread(server);
    serverThread.start();

    Scanner scanner = new Scanner(System.in);
    try {
      while (true) {
        if (scanner.next().equals("exit")) {
          server.shutdown();
          break;
        }
      }
    } catch (IOException e) {
      LOGGER.error("IOException while shutting down server: " + e.getMessage());
      System.out.println("IOException while shutting down server: " + e.getMessage());
    }
  }
}
