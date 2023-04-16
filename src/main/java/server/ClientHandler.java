package server;

import client.ClientProtocol;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
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

  private final Logger LOGGER;

  // Variables required for the game
  // TODO: replace with false and add check to set correct players canJump to true
  public boolean canJump = true;
  public boolean ready = false;

  /** Is in charge of a single client. */
  public ClientHandler(Socket clientSocket, Server server) throws IOException {
    this.client = clientSocket;
    this.in = new BufferedReader(new InputStreamReader(client.getInputStream()));
    this.out = new PrintWriter(client.getOutputStream(), true);
    this.server = server;
    this.LOGGER = LogManager.getLogger(getClass());
  }
  /**
   * Handles the client's input. If the client sends "exit", the server shuts down. If the client
   * sends "say", the server broadcasts the message to all clients.
   */
  @Override
  public void run() {
    while (this.running) {
      // Receive message and split it into an array
      String message = this.receiveFromClient();
      if (message != null) {
        String[] command = message.split(ServerProtocol.SEPARATOR.toString());
        this.protocolSwitch(command);
      }
    }
    try {
      client.close();
      in.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /** Sends ping to client to check if the connection is still alive. */
  protected void ping() {
    String command = ServerProtocol.SERVER_PING.toString();
    this.out.println(command);
  }
  /** Sends a Server_PONG message to the client (meant as a response to the CLIENT_PING message) */
  protected void pong() {
    String command = ServerProtocol.SERVER_PONG.toString();
    this.out.println(command);
  }

  /**
   * The client linked to this ClientHandler wants to send a message to all clients on the server.
   *
   * <p>See {@link ServerProtocol#SEND_PUBLIC_MESSAGE}
   */
  private void sendPublicMessage(String message) {
    String output =
        ServerProtocol.SEND_PUBLIC_MESSAGE.toString()
            + ServerProtocol.SEPARATOR
            + this.username
            + ServerProtocol.SEPARATOR
            + message;

    for (ClientHandler client : this.server.getClientHandlers()) {
      client.out.println(output);
    }
  }

  /**
   * Sets the ready status of the client to true. The game only starts when the
   * ready status of all clients is set to true
   */
  protected void readyUp() {
    this.ready = true;
  }

  /**
   * The client linked to this ClientHandler wants to send a message to another client on the
   * server.
   *
   * <p>See {@link ServerProtocol#SEND_PRIVATE_MESSAGE}
   */
  private void sendPrivateMessage(String recipient, String message) {
    String output =
        ServerProtocol.SEND_PRIVATE_MESSAGE.toString()
            + ServerProtocol.SEPARATOR
            + this.username
            + ServerProtocol.SEPARATOR
            + message;

    ClientHandler recipientHandler = this.server.getClientHandler(recipient);
    if (recipientHandler != null && recipientHandler != this) {
      recipientHandler.out.println(output);
      this.out.println(output);
    } else if (recipientHandler == null) {
      this.out.println(
          ServerProtocol.NO_USER_FOUND.toString() + ServerProtocol.SEPARATOR + recipient);
      this.LOGGER.error(
          "ClientHandler "
              + this.username
              + " tried to send a message to "
              + recipient
              + ", but the recipient doesn't exist.");
    }
  }
  /**
   * The client linked to this ClientHandler wants to send a message to all clients in the lobby.
   *
   * <p>See {@link ServerProtocol#SEND_LOBBY_MESSAGE}
   *
   * @param message The message to send
   */
  private void sendLobbyMessage(String message) {
    String command =
        ServerProtocol.SEND_LOBBY_MESSAGE.toString()
            + ServerProtocol.SEPARATOR
            + this.username
            + ServerProtocol.SEPARATOR
            + message;

    for (ClientHandler client : this.lobby.getClientHandlers()) {
      client.out.println(command);
    }
  }

  /** Receives commands from the client. */
  private String receiveFromClient() {
    try {
      return this.in.readLine();
    } catch (IOException e) {
      this.LOGGER.error(
          "ClientHandler " + this.username + " couldn't receive message from client.");
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
          case EXIT -> this.server.removeClient(this);
          case SET_USERNAME -> this.setUsername(command[1]);
          case GET_FULL_SERVER_LIST -> {
            this.updateClientList();
            this.updateLobbyList();
          }
          case SEND_PUBLIC_MESSAGE -> this.sendPublicMessage(command[1]);
          case SEND_PRIVATE_MESSAGE -> this.sendPrivateMessage(command[1], command[2]);
          case SEND_LOBBY_MESSAGE -> this.sendLobbyMessage(command[1]);
          case CLIENT_PING -> this.pong();
          case CLIENT_PONG -> this.resetClientStatus();
          case CREATE_LOBBY -> this.server.createLobby(command[1], command[2], this);
          case JOIN_LOBBY -> this.server.joinLobby(command[1], command[2], this);
          case GET_CLIENTS_LOBBY -> {
            if (this.lobby != null) this.listLobby();
          }
          case GET_CLIENTS_SERVER -> this.sendClientList(this.server.getClientHandlers());
          case TOGGLE_READY_STATUS -> this.setToggleReady(command[1]);
          case EXIT_LOBBY -> {
            if (this.lobby != null) this.lobby.removeClient(this);
          }
          case START_GAME_LOOP -> this.startGameLoop();
          case READY_UP -> this.readyUp();
          case REQUEST_JUMP -> this.requestJump();
          case REQUEST_PAUSE -> this.requestPause();

          default -> System.out.println("[CLIENT_HANDLER] Unknown command: " + protocol);
        }
      }
    } catch (IllegalArgumentException e) {
      System.out.println("[CLIENT_HANDLER] Unknown command: " + command[0]);
    }
  }

  /**
   * Starts the game loop of the game in this client's lobby, and sends a protocol command to all clients
   * to start their game loops.
   */
  private void startGameLoop() {
    this.lobby.getGame().startGameLoop();
    this.lobby.sendGameCommandToAllClients(ClientProtocol.START_GAME_LOOP.toString());
  }

  /** Resets noAnswerCounter. */
  private void resetClientStatus() {
    this.clientConnected = true;
    this.noAnswerCounter = 0;
  }

  /** Returns the username */
  protected String getUsername() {
    return this.username;
  }

  /** The client has clicked the ready button. */
  private void setToggleReady(String isReady) {
    this.lobby.toggleClientReady(this, Boolean.parseBoolean(isReady));
  }

  /**
   * Called when the client has sent a new username in. If the username is already taken, a random
   * suffix is added to the username and the method is called recursively.
   */
  private void setUsername(String username) {
    ClientHandler client = this.server.getClientHandler(username);

    if (client == null) {
      if (this.username == null) {
        this.LOGGER.info("Connected client with username " + username + ".");
      } else {
        this.LOGGER.info("Client " + this.username + " changed username to " + username + ".");
      }

      this.username = username;
      String message =
          ServerProtocol.USERNAME_SET_TO.toString() + ServerProtocol.SEPARATOR + this.username;
      this.out.println(message);

      this.server.updateClientList();
      this.server.updateLobbyList();
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
    this.out.println(
        ServerProtocol.LOBBY_JOINED.toString() + ServerProtocol.SEPARATOR + lobby.getName());
    this.listLobby();
  }

  /**
   * Sends a list of clients in the lobby to the client. This includes whether the client has
   * toggled ready or not.
   */
  protected void listLobby() {
    if (this.lobby != null) {
      String command = this.lobby.listLobby();

      this.out.println(command);
    }
  }

  /**
   * return this.lobby Is called from the server when a Client disconnects, so it can be removed
   * from the lobby
   */
  protected Lobby getLobby() {
    return this.lobby;
  }

  /**
   * Sends a list of clients to the client.
   *
   * @param clients The list of clients to send
   */
  protected void sendClientList(ArrayList<ClientHandler> clients) {
    String command =
        ServerProtocol.UPDATE_LOBBY_LIST.toString()
            + ServerProtocol.SEPARATOR
            + clients.stream().map(ClientHandler::getUsername).collect(Collectors.joining(" "));
    System.out.println(command);
    this.out.println(command);
  }
  /**
   * Called when the client leaves a lobby.
   *
   * @see Server#removeClient(ClientHandler)
   */
  protected void exitLobby() {
    String command =
        ServerProtocol.LOBBY_EXITED.toString() + ServerProtocol.SEPARATOR + this.lobby.getName();
    this.out.println(command);
    this.lobby = null;
  }

  /** Updates the lobbyList and prints it out */
  public void updateLobbyList() {
    String[][] lobbyInfo = this.server.listLobbies();

    StringBuilder command =
        new StringBuilder(ServerProtocol.UPDATE_FULL_LIST.toString())
            .append(ServerProtocol.SEPARATOR);

    for (int i = 0; i < lobbyInfo.length; i++) {
      command.append(String.join(" ", lobbyInfo[i]));
      if (i < lobbyInfo.length - 1) {
        command.append(ServerProtocol.LOBBY_INFO_SEPARATOR);
      }
    }

    this.out.println(command);
  }

  /** Updates the list of the clients */
  public void updateClientList() {
    ArrayList<ClientHandler> clients = this.server.getClientHandlers();

    String command =
        ServerProtocol.UPDATE_CLIENT_LIST.toString()
            + ServerProtocol.SEPARATOR
            + clients.stream().map(ClientHandler::getUsername).collect(Collectors.joining(" "));

    this.out.println(command);
  }

  /** Called from {@link ServerGame} to tell the client that the game has started. */
  public void startGame() {
    this.out.println(ServerProtocol.START_GAME);
  }

  private void requestJump() {
    if (this.lobby.getGame().handleJumpRequest(this)){
      for(ClientHandler client : this.lobby.getClientHandlers()) {
        client.out.println(ServerProtocol.JUMP.toString());
      }
    }
  }

  /**
   * Called when the client has requested to toggle pause. The game is paused, and a protocol command is sent back
   * to all clients in the lobby to toggle pause on their end.
   */
  private void requestPause() {
    System.out.println("Request pause Executed");
    this.lobby.getGame().setPause();
    for(ClientHandler client : this.lobby.getClientHandlers()) {
      client.out.println(ServerProtocol.TOGGLE_PAUSE.toString());
    }
  }

  /**
   * Sends StartGameLoop command to the client associated with this ClientHandler
   */
  public void startClientGameLoop() {
    String command = ClientProtocol.START_GAME_LOOP.toString();
    this.out.println(command);
  }

  /**
   * Sends a command to the client to update the position of the player
   * @param command the ServerProtocol command POSITION_UPDATE
   */
  public void positionUpdate(String command){
    this.out.println(command);
  }

}


