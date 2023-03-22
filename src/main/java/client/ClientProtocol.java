package client;

import server.ServerProtocol;

/**
 * The Client protocol for Hues in the Air
 *
 * <p>These are the commands that the client can send to the server. See {@link ServerProtocol} for
 * the commands that the server can send to the client.
 */
public enum ClientProtocol {

  /**
   * Symbol inputted by the client in the console to indicate that the following input is a command.
   */
  COMMAND_SYMBOL(0) {
    public String toString() {
      return "!";
    }
  },
  /**
   * Set client username
   *
   * <p>Format: SET_USERNAME<Separator>username
   */
  SET_USERNAME(1),

  /**
   * Send a chat message to lobby
   *
   * <p>Format: SEND_MESSAGE_LOBBY<Separator>message
   */
  SEND_MESSAGE_LOBBY(1),

  /**
   * This client wants to send a private message to another client.
   *
   * <p>Protocol format: SEND_MESSAGE_CLIENT<SEPARATOR>receiver.username<SEPARATOR>message
   */
  WHISPER(2),

  /**
   * Send a chat message to the whole server
   *
   * <p>Format: SEND_MESSAGE_SERVER<Separator>username<Separator>message
   */
  BROADCAST(1),

  /** Client is exitting the program. */
  LOGOUT(0),

  /**
   * Client wants to join a lobby
   *
   * <p>Format: JOIN_LOBBY<Separator>lobbyName<Separator>password
   */
  JOIN_LOBBY(2),

  /**
   * Client wants to create a lobby
   *
   * <p>Format: CREATE_LOBBY<Separator>lobbyName<Separator>password
   */
  CREATE_LOBBY(2),

  /** Client requests to know their own username */
  WHOAMI(0),

  /** Client wants to know the name of the other players */
  LIST_SERVER(0),

  LIST_LOBBY(0),

  PING(0);

  private final int numArgs;

  ClientProtocol(int numArgs) {
    this.numArgs = numArgs;
  }

  public int getNumArgs() {
    return this.numArgs;
  }
}
