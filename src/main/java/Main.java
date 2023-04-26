import client.Client;
import javafx.application.Application;
import server.ServerMain;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Main class of the project. Launches a server or a client depending on the arguments passed to it.
 */
public class Main {

  private static final Logger LOGGER = LogManager.getLogger(Main.class);

  /**
   * Main class of the project. Uses a Logger to save what's happening when it runs.
   * @param args The arguments passed to the program
   */
  public static void main(String[] args) {
    if (args.length >= 1) {
      switch (args[0]) {
        case "server" -> {
          if (args.length == 2) {
            String port = args[1];
            ServerMain.main(new String[] {port});
          } else {
            LOGGER.error(
                "Wrong number of arguments for option <server>. Please try again in the following format: "
                    + "<server PORT>");
          }
        }
        case "client" -> {
          if (args.length == 1) {
            Application.launch(Client.class, "", "", "");
          } else if (args.length <= 3) {
            String[] split = args[1].split(":");
            if (split.length == 2) {
              String hostAddress = split[0];
              String port = split[1];
              String username = args.length == 3 ? args[2] : "";
              Application.launch(Client.class, hostAddress, port, username);
            } else {
              LOGGER.error(
                  "The given arguments are not valid. Please try again in the following format: "
                      + "<client hostAddress:port username>");
            }
          } else {
            LOGGER.error(
                "Too many arguments. Please try again in the following format: "
                    + "<client hostAddress:port username>");
          }
        }
        default -> LOGGER.error(
            "First argument not recognised. Please try again in the following format: "
                + "<server hostAddress:port> or <client hostAddress:port username>");
      }
    } else {
      LOGGER.error(
          "No arguments given. Please try again in the following format: "
              + "<server hostAddress:port> or <client hostAddress:port username>");
    }
  }
}
