package server;

import client.ClientProtocol;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Handles the connection to a single client. */
public class ClientHandler implements Runnable {

  /** The client's socket. */
  private final Socket client;
  /** The client is connected. */
  private boolean clientConnected = true;
  /** The number of times there is no answer. */
  private int noAnswerCounter = 0;

  /** Input stream. */
  private final BufferedReader in;
  /** Output stream. */
  private final PrintWriter out;

  /** The server: used to access the list of clients. */
  private final Server server;
  /** The ClientHandler is running. */
  private boolean running = true;

  /** The Clients username. */
  private String username;
  /** The Lobby in which the Client is. */
  private Lobby lobby;
  /** Logger from the log4j2 library. */
  private final Logger LOGGER;

  /**
   * Is in charge of a single client.
   *
   * @param clientSocket The client's socket
   * @param theServer The server
   * @throws IOException If getInputStream() or getOutputStream() fails
   */
  public ClientHandler(final Socket clientSocket, final Server theServer)
          throws IOException {
    this.client = clientSocket;
    this.in = new BufferedReader(
            new InputStreamReader(client.getInputStream()));
    this.out = new PrintWriter(client.getOutputStream(), true);
    this.server = theServer;
    this.LOGGER = LogManager.getLogger(getClass());
  }

  /**
   * @return if client is connected
   */
  public boolean getClientConnected() {
    return this.clientConnected;
  }

  /**
   * Sets if the client is connected.
   *
   * @param isConnected if the Client is connected
   */
  public void setClientConnected(final boolean isConnected) {
    this.clientConnected = isConnected;
  }

  /**
   * Return the number of times there's no answer.
   *
   * @return noAnswerCounter
   */
  public int getNoAnswerCounter() {
    return this.noAnswerCounter;
  }

  /**
   * Sets the number of times there is no answer.
   *
   * @param noAnswer number of times there is no answer
   */
  public void setNoAnswerCounter(final int noAnswer) {
    this.noAnswerCounter = noAnswer;
  }

  /**
   * Sets the ClientHandler to run.
   *
   * @param clientRunning if the Client is running
   */
  public void setRunning(final boolean clientRunning) {
    this.running = clientRunning;
  }

  /**
   * Handles the client's input. If the client sends "exit",
   * the server shuts down.If the client sends "say",
   * the server broadcasts the message to all clients.
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
  /** Sends a Server_PONG message to the client.
   *  (meant as a response to the CLIENT_PING message) */
  protected void pong() {
    String command = ServerProtocol.SERVER_PONG.toString();
    this.out.println(command);
  }

  /**
   * The client linked to this ClientHandler wants
   * to send a message to all clients on the server.
   *
   * @param message the message to be sent
   *     <p>See {@link ServerProtocol#SEND_PUBLIC_MESSAGE}
   */
  private void sendPublicMessage(final String message) {
    String output =
        ServerProtocol.SEND_PUBLIC_MESSAGE.toString()
            + ServerProtocol.SEPARATOR
            + this.username
            + ServerProtocol.SEPARATOR
            + message;

    for (ClientHandler c : this.server.getClientHandlers()) {
      c.out.println(output);
    }
  }

  /**
   * The client linked to this ClientHandler
   * wants to send a message to another client on the server.
   *
   * @param recipient the one that receives the message
   * @param message the message to be sent
   *     <p>See {@link ServerProtocol#SEND_PRIVATE_MESSAGE}
   */
  private void sendPrivateMessage(
          final String recipient, final String message) {
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
          ServerProtocol.NO_USER_FOUND.toString()
                  + ServerProtocol.SEPARATOR + recipient);
      this.LOGGER.error(
          "ClientHandler "
              + this.username
              + " tried to send a message to "
              + recipient
              + ", but the recipient doesn't exist.");
    }
  }
  /**
   * The client linked to this ClientHandler wants
   * to send a message to all clients in the lobby.
   *
   * <p>See {@link ServerProtocol#SEND_LOBBY_MESSAGE}
   *
   * @param message The message to send
   */
  private void sendLobbyMessage(final String message) {
    String command =
        ServerProtocol.SEND_LOBBY_MESSAGE.toString()
            + ServerProtocol.SEPARATOR
            + this.username
            + ServerProtocol.SEPARATOR
            + message;

    for (ClientHandler theClient : this.lobby.getClientHandlers()) {
      theClient.out.println(command);
    }
  }

  /**
   * Receives commands from the client.
   *
   * @return the command in a String
   */
  private String receiveFromClient() {
    try {
      return this.in.readLine();
    } catch (IOException e) {
      this.LOGGER.error(
          "ClientHandler " + this.username
                  + " couldn't receive message from client.");
      return null;
    }
  }

  /**
   * Called from {@link #receiveFromClient()}.
   *
   * @param command to execute
   *     <p>Goes over the different commands of {@link ClientProtocol}
   *               and calls the appropriate
   *     method.
   */
  private void protocolSwitch(final String[] command) {
    try {
      ClientProtocol protocol = ClientProtocol.valueOf(command[0]);

      if (protocol.getNumArgs() == command.length - 1) {
        switch (protocol) {
          case EXIT -> this.server.removeClient(this);
          case SET_USERNAME -> this.setUsername(command[1]);
          case SEND_PUBLIC_MESSAGE -> this.sendPublicMessage(command[1]);
          case SEND_PRIVATE_MESSAGE
                  -> this.sendPrivateMessage(command[1], command[2]);
          case SEND_LOBBY_MESSAGE -> this.sendLobbyMessage(command[1]);
          case CLIENT_PING -> this.pong();
          case CLIENT_PONG -> this.resetClientStatus();
          case CREATE_LOBBY
                  -> this.server.createLobby(command[1], command[2], this);
          case JOIN_LOBBY
                  -> this.server.joinLobby(command[1], command[2], this);
          case GET_FULL_SERVER_LIST -> {
            this.updateClientList();
            this.updateLobbyList();
          }
          case GET_FULL_MENU_LISTS -> {
            this.updateClientList();
            this.updateLobbyList();
            this.updateGameList();
          }
          case GET_FULL_LOBBY_LIST -> this.lobby.updateLobbyList();
          case TOGGLE_READY_STATUS -> this.setToggleReady(command[1]);
          case EXIT_LOBBY -> {
            if (this.lobby != null) {
              this.lobby.removeClient(this);
            }
          }
          case SPACE_BAR_PRESSED -> this.spaceBarPressed();
          case REQUEST_CRITICAL_BLOCKS
                  -> this.getLobby().getGame().sendCriticalBlocks();
          case REQUEST_END_GAME -> this.getLobby().getGame().endGame();
          case SKIP_LEVEL -> this.getLobby().getGame().skipLevel();
          case SET_IMMORTAL -> this.getLobby().getGame().setImmortal();
          case SET_MORTAL -> this.getLobby().getGame().setMortal();

          default -> LOGGER.error(
              "ClientHandler " + this.username
                      + " sent an invalid command: " + command[0]);
        }
      }
    } catch (IllegalArgumentException | NullPointerException e) {
      LOGGER.error("ClientHandler " + this.username
              + " sent an invalid command: " + command[0]);
    }
  }

  /** Resets noAnswerCounter. */
  private void resetClientStatus() {
    this.clientConnected = true;
    this.noAnswerCounter = 0;
  }

  /**
   * Returns the username.
   *
   * @return the username of the client
   */
  protected String getUsername() {
    return this.username;
  }

  /**
   * The client has clicked the ready button.
   *
   * @param isReady the client is ready
   */
  private void setToggleReady(final String isReady) {
    this.lobby.toggleClientReady(this, Boolean.parseBoolean(isReady));
  }

  /**
   * Called when the client has sent a new username in.
   * If the username is already taken, a random suffix is added
   * to the username and the method is called recursively.
   *
   * @param newUsername new username of the client
   */
  private void setUsername(final String newUsername) {
    ClientHandler clientHandler = this.server.getClientHandler(newUsername);

    if (clientHandler == null) {
      if (this.username == null) {
        this.LOGGER.info("Connected client with username " + newUsername + ".");
      } else {
        this.LOGGER.info("Client " + this.username
                + " changed username to " + newUsername + ".");
      }

      this.username = newUsername;
      String message =
          ServerProtocol.USERNAME_SET_TO.toString()
                  + ServerProtocol.SEPARATOR + this.username;
      this.out.println(message);

      this.server.updateClientList();
      this.server.updateLobbyList();
    } else {
      String[] suffixes = {
        " the Great", " the Wise", " the Brave",
              " the Strong", " the Mighty", " the Magnificent"
      };
      int random = (int) (Math.random() * suffixes.length);
      String output = newUsername + suffixes[random];
      setUsername(output.replaceAll(" ", "_"));
    }
  }

  /**
   * Upon entry of a lobby
   * (handled by {@link Server#joinLobby(String, String, ClientHandler)}), the
   * client is informed of the success of the operation.
   *
   * @param theLobby The lobby the client has entered
   */
  protected void enterLobby(final Lobby theLobby) {
    this.lobby = theLobby;
    this.out.println(
        ServerProtocol.LOBBY_JOINED.toString()
                + ServerProtocol.SEPARATOR + theLobby.getName());
    this.listLobby();
  }

  /**
   * Sends a list of clients in the lobby to the client.
   * This includes whether the client has
   * toggled ready or not.
   */
  protected void listLobby() {
    if (this.lobby != null) {
      String command = this.lobby.listLobby();

      this.out.println(command);
    }
  }

  /**
   * Is called from the server when a Client disconnects,
   * so it can be removed from the lobby.
   *
   * @return Lobby the client is in
   */
  protected Lobby getLobby() {
    return this.lobby;
  }

  /**
   * Called when the client leaves a lobby.
   *
   * @see Server#removeClient(ClientHandler)
   */
  protected void exitLobby() {
    String command =
        ServerProtocol.LOBBY_EXITED.toString()
                + ServerProtocol.SEPARATOR + this.lobby.getName();
    this.out.println(command);
    this.lobby = null;
  }

  /**
   * Sends the list of lobbies in the server and their clients
   * to the client. The client uses this information in the menu.
   */
  public void updateLobbyList() {
    String[][] lobbyInfo = this.server.listLobbies();

    StringBuilder command =
        new StringBuilder(ServerProtocol.UPDATE_FULL_LIST.toString())
            .append(ServerProtocol.SEPARATOR);

    for (int i = 0; i < lobbyInfo.length; i++) {
      command.append(String.join(" ", lobbyInfo[i]));
      if (i < lobbyInfo.length - 1) {
        command.append(ServerProtocol.SUBSEPARATOR);
      }
    }

    this.out.println(command);
  }

  /**
   * Sends the list of all clients connected to the server
   * to the client. The client uses this information in the menu.
   */
  public void updateClientList() {
    ArrayList<ClientHandler> clients = this.server.getClientHandlers();

    String command =
        ServerProtocol.UPDATE_CLIENT_LIST.toString()
            + ServerProtocol.SEPARATOR
            + clients.stream().map(ClientHandler::getUsername)
                .collect(Collectors.joining(" "));

    this.out.println(command);
  }

  /** Sends the list of all games that have been played
   *  or are being played to the client. */
  public void updateGameList() {
    List<String> highscores = this.server.getHighscores();


    String command = ServerProtocol.UPDATE_GAME_LIST.toString()
            + ServerProtocol.SEPARATOR;

    command += highscores.stream().
            collect(Collectors.joining(ServerProtocol.SUBSEPARATOR.toString()));

    this.out.println(command);
  }

  /** Called from {@link ServerGame} to tell the client
   *  that the game has started. */
  public void startGame() {
    this.out.println(ServerProtocol.START_GAME);
  }

  private void spaceBarPressed() {
    this.lobby.getGame().spaceBarPressed(this);
  }

  /**
   * Sends a command to the client to update the position of the player.
   *
   * @param command the ServerProtocol command POSITION_UPDATE
   */
  public void positionUpdate(final String command) {
    this.out.println(command);
  }

  /**
   * Sends a command to the client to inform them of the position
   * and colour of the critical blocks in the level.
   *
   * @param command the ServerProtocol command
   * {@link ServerProtocol#SEND_CRITICAL_BLOCKS}
   */
  public void sendCriticalBlocks(final String command) {
    this.out.println(command);
  }

  /**
   * Informs the client that the game has ended.
   * The client can then exit the game screen and go back to the lobby.
   */
  public void gameEnded() {
    this.out.println(ServerProtocol.GAME_ENDED);
  }

  /**
   * Informs the client of the path of the level to load.
   * Called upon game start and when a new level is loaded.
   *
   * @param levelPath The path of the level to load
   */
  public void sendLevelPath(final String levelPath) {
    this.out.println(ServerProtocol.LOAD_LEVEL.toString()
            + ServerProtocol.SEPARATOR + levelPath);
  }

  /**
   * Informs the clients in the game of the amount of lives they have left
   * and how many levels they have completed.
   *
   * @param command The command to send to the client
   */
  public void gameStatusUpdate(final String command) {
    this.out.println(command);
  }

  /**
   * Informs the client of the position of the rotation point in the game.
   * Called when the cube has just jumped.
   *
   * @param command The command to send to the client
   */
  public void jumpUpdate(final String command) {
    this.out.println(command);
  }
}
