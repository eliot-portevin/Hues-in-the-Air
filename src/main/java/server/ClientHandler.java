package server;

import client.ClientProtocol;
import static shared.Encryption.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ClientHandler implements Runnable {

  // The client's socket
  private final Socket client;
  boolean clientConnected = true;
  int noAnswerCounter = 0;

  // Input and output streams
  private final BufferedReader in;
  private final PrintWriter out;

  // The server: used to access the list of clients
  private final Server server;

  protected boolean running = true;

  // Client values
  private String username;
  private Lobby lobby;
  private int missedConnections = 0;

  /** Is in charge of a single client. */
  public ClientHandler(Socket clientSocket, Server server) throws IOException {
    this.client = clientSocket;
    this.in = new BufferedReader(new InputStreamReader(client.getInputStream()));
    this.out = new PrintWriter(client.getOutputStream(), true);
    this.server = server;

    this.requestUsernameFromClient();
  }
  /**
   * Handles the client's input. If the client sends "exit", the server shuts down. If the client
   * sends "say", the server broadcasts the message to all clients.
   */
  @Override
  public void run() {
    while (this.running) {
      // Receive message, decrypt it and split it into an array
      String message = this.receiveFromClient();
      String[] command = decrypt(message).split(ServerProtocol.SEPARATOR.toString());
      this.protocolSwitch(command);
    }
    try {
      client.close();
      in.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  protected void ping() {
    String command = ServerProtocol.SERVER_PING.toString();
    this.out.println(encrypt(command));
  }

  protected void pong() {
    String command = ServerProtocol.SERVER_PONG.toString();
    this.out.println(encrypt(command));
  }

  /**
   * The client linked to this ClientHandler wants to send a message to all clients on the server.
   *
   * <p>See {@link ServerProtocol#BROADCAST}
   */
  private void sendMessageServer(String message) {
    String output =
        ServerProtocol.BROADCAST.toString()
            + ServerProtocol.SEPARATOR
            + this.username
            + ServerProtocol.SEPARATOR
            + message;

    output = encrypt(output);

    for (ClientHandler client : this.server.getClientHandlers()) {
      client.out.println(output);
    }
  }

  /**
   * The client linked to this ClientHandler wants to send a message to another client on the
   * server.
   *
   * <p>See {@link ServerProtocol#WHISPER}
   */
  private void sendMessageClient(String recipient, String message) {
    String output =
        ServerProtocol.WHISPER.toString()
            + ServerProtocol.SEPARATOR
            + this.username
            + ServerProtocol.SEPARATOR
            + message;

    ClientHandler recipientHandler = this.server.getClientHandler(recipient);
    if (recipientHandler != null) {
      output = encrypt(output);
      recipientHandler.out.println(output);
      this.out.println(output);
    } else {
      System.out.println("Didn't find the user");
      this.out.println(
          encrypt(ServerProtocol.NO_USER_FOUND.toString() + ServerProtocol.SEPARATOR + recipient));
    }
  }

  private void sendMessageLobby(String message) {
    String command =
        ServerProtocol.SEND_MESSAGE_LOBBY.toString()
            + ServerProtocol.SEPARATOR
            + this.username
            + ServerProtocol.SEPARATOR
            + message;

    for (ClientHandler client : this.lobby.getClientHandlers()) {
      client.out.println(encrypt(command));
    }
  }

  /** Receives commands from the client. */
  private String receiveFromClient() {
    try {
      // TODO Add Logger
      return this.in.readLine();
    } catch (IOException e) {
      System.err.println(
          "[CLIENT_HANDLER] " + this.username + " failed to receive message from client");
      System.out.println(Arrays.toString(e.getStackTrace()));
      return null;
    }
  }

  /**
   * Called from {@link #receiveFromClient()}.
   *
   * <p>Goes over the different commands of {@link ClientProtocol} and calls the appropriate method.
   */
  private void protocolSwitch(String[] command) {
    try {
      ClientProtocol protocol = ClientProtocol.valueOf(command[0]);

      if (protocol.getNumArgs() == command.length - 1) {
        switch (protocol) {
          case LOGOUT -> this.server.removeClient(this);
          case SET_USERNAME -> this.setUsername(command[1]);
          case BROADCAST -> this.sendMessageServer(command[1]);
          case WHISPER -> this.sendMessageClient(command[1], command[2]);
          case SEND_MESSAGE_LOBBY -> this.sendMessageLobby(command[1]);
          case CLIENT_PING -> this.pong();
          case CLIENT_PONG -> this.resetClientStatus();
          case CREATE_LOBBY -> this.server.createLobby(command[1], command[2], this);
          case JOIN_LOBBY -> this.server.joinLobby(command[1], command[2], this);
          case LIST_LOBBY -> {
            if (this.lobby != null) this.sendClientList(this.lobby.getClientHandlers());
          }
          case LIST_SERVER -> this.sendClientList(this.server.getClientHandlers());

          default -> System.out.println("[CLIENT_HANDLER] Unknown command: " + protocol);
        }
      }
    } catch (IllegalArgumentException e) {
      System.out.println("[CLIENT_HANDLER] Unknown command: " + command[0]);
    }
  }

  private void resetClientStatus() {
    this.clientConnected = true;
    this.noAnswerCounter = 0;
  }

  /** Requests the client's username upon connection. */
  private void requestUsernameFromClient() {
    // TODO Add Logger
    String message = encrypt(ServerProtocol.NO_USERNAME_SET.toString());
    this.out.println(message);
  }

  protected String getUsername() {
    return this.username;
  }

  /**
   * Called when the client has sent a new username in. If the username is already taken, a random
   * suffix is added to the username and the method is called recursively.
   */
  private void setUsername(String username) {
    if (this.username != null) {
      if (this.username.equals(username)) {
        return;
      }
    }

    ClientHandler client = this.server.getClientHandler(username);

    if (client == null) {
      System.out.printf("%s changed their username to %s.\n", this.username, username);
      this.username = username;
      String message =
          ServerProtocol.USERNAME_SET_TO.toString() + ServerProtocol.SEPARATOR + this.username;
      this.out.println(encrypt(message));
    } else {
      String[] suffixes = {
        " the Great", " the Wise", " the Brave", " the Strong", " the Mighty", " the Magnificent"
      };
      int random = (int) (Math.random() * suffixes.length);
      String output = username + suffixes[random];
      setUsername(output.replaceAll(" ", "_"));
    }
  }

  /**
   * Upon entry of a lobby (handled by {@link Server#joinLobby(String, String, ClientHandler)}), the
   * client is informed of the success of the operation.
   */
  protected void enterLobby(Lobby lobby) {
    this.lobby = lobby;
    System.out.println(this.username + " entered lobby " + lobby.getName());
    this.sendMessageLobby(this.username + " entered the lobby " + this.lobby.getName() + ".");
  }

  protected Lobby getLobby() {
    return this.lobby;
  }

  protected ArrayList<ClientHandler> getClients() {
    return this.server.getClientHandlers();
  }

  protected void sendClientList(ArrayList<ClientHandler> clients) {
    String command =
        ServerProtocol.SEND_CLIENT_LIST.toString()
            + ServerProtocol.SEPARATOR
            + clients.stream().map(ClientHandler::getUsername).collect(Collectors.joining(" "));
    this.out.println(encrypt(command));
  }
}
