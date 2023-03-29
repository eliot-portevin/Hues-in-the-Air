package client;

import server.ServerProtocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;

/** Sends commands from client to server. */
public class ServerOut implements Runnable {

  private final Socket serverSocket;
  private final PrintWriter out;
  private final BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
  private final Client client;

  protected Boolean running = true;

  /** Creates an instance of ServerOut */
  public ServerOut(Socket serverSocket, Client client) throws IOException {
    this.serverSocket = serverSocket;
    this.out = new PrintWriter(this.serverSocket.getOutputStream(), true);
    this.client = client;
  }

  /** From the Runnable interface. Runs the ServerOut thread to send commands to the server */
  @Override
  public void run() {
    try {
      while (this.running) {
        String command = this.keyboard.readLine();

        if (command != null) {
          System.out.print("> ");
          this.handleCommand(command);
        }
      }
      try {
        this.serverSocket.close();
        this.out.close();
      } catch (IOException e) {
        System.err.println(e.getMessage());
      }
    } catch (IOException e) {
      System.err.println("[ServerOut]: " + e.getMessage());
      e.printStackTrace();
    }
    // Close the socket and the input stream
    try {
      this.serverSocket.close();
      this.keyboard.close();
    } catch (IOException e) {
      System.err.println(
          "[CLIENT] Failed to close serverSocket and input stream: " + e.getMessage());
      e.printStackTrace();
    }
  }

  protected void sendToServer(String message) {
    if (this.validateMessage(message)) {
      this.out.println(message);
    }
  }

  private Boolean validateMessage(String message) {
    if (message == null) {
      System.out.println("[SERVER_OUT] Message is null");
      return false;
    } else {
      String[] command = message.split(ServerProtocol.SEPARATOR.toString());
      if (command.length > ClientProtocol.valueOf(command[0]).getNumArgs() + 1) {
        System.out.print(
            "[SERVER_OUT] Tried to send too many arguments: "
                + Arrays.toString(command)
                + "\n> Would you just have happened to have "
                + ServerProtocol.SEPARATOR.toString()
                + " in your message?" +
                "\n> If you are trying to test this program's security, feel free to go away.\n> ");
        return false;
      }
    }
    return true;
  }

  private void handleCommand(String command) {
    String commandSymbol = ClientProtocol.COMMAND_SYMBOL.toString();

    if (command.startsWith(commandSymbol)) {
      int firstSpace = command.indexOf(" ");
      if (firstSpace == -1) {
        firstSpace = command.length();
      }

      try {
        ClientProtocol protocol =
            ClientProtocol.valueOf(
                command.substring(0, firstSpace).replace(commandSymbol, "").toUpperCase());

        // If the command has no arguments
        if (firstSpace == command.length()) {
          switch (protocol) {
            case EXIT -> this.client.exit();
            case WHOAMI -> this.client.whoami();
            case LIST_LOBBY -> this.client.listClientsLobby();
            case LIST_SERVER -> this.client.listClientsServer();
            case EXIT_LOBBY -> this.client.exitLobby();

              /*
               Methods requiring further inputs from user. These can also be called directly with the
               required arguments.
              */
            case SET_USERNAME -> this.client.setUsername();
            case BROADCAST -> this.client.sendMessageServer();
            case WHISPER -> this.client.sendMessageClient();
            case SEND_MESSAGE_LOBBY -> this.client.sendMessageLobby();
            case CREATE_LOBBY -> this.client.createLobby();
            case JOIN_LOBBY -> this.client.joinLobby();
          }
        } else {
          String[] args = command.substring(firstSpace + 1).split(" ");

          switch (protocol) {
            case BROADCAST -> this.client.sendMessageServer(String.join(" ", args));
            case WHISPER -> this.client.sendMessageClient(
                args[0], String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
            case SEND_MESSAGE_LOBBY -> this.client.sendMessageLobby(String.join(" ", args));
            case SET_USERNAME -> this.client.setUsername(args[0].replaceAll(" ", "_"));
            case CREATE_LOBBY -> this.client.createLobby(args[0], args[1]);
            case JOIN_LOBBY -> this.client.joinLobby(args[0], args[1]);
          }
        }
      } catch (IllegalArgumentException e) {
        System.out.print("[SERVER_OUT] Command '" + command.substring(1) + "' not recognized.\n> ");
      }

    } else {
      System.out.print("[SERVER_OUT] Command does not start with command symbol.\n> ");
    }
  }
}
