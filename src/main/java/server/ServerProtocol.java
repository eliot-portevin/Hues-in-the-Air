package server;

import client.ClientProtocol;

/**
 * Server protocol for Hues in the Air
 *
 * <p>These are the commands that the server can send to the client. See {@link ClientProtocol} for
 * the commands that the client can send to the server.
 */
public enum ServerProtocol {
  /**
   * Separator for the protocol
   *
   * <p>This is used to separate arguments in the protocol.
   */
  SEPARATOR(0) {
    public String toString() {
      return "<&!>";
    }
  },

  /** Separator used for lobby lists */
  LOBBY_INFO_SEPARATOR(0) {
    public String toString() {
      return "<&?>";
    }
  },

  /** Inform client that the username is already taken */
  USERNAME_SET_TO(1),

  /** No user with that username was found. Called from {@link ClientHandler}. */
  NO_USER_FOUND(1),

  /** A message is being sent to another client. */
  SEND_PRIVATE_MESSAGE(2),

  /** A message is being sent to the whole server. */
  SEND_PUBLIC_MESSAGE(2),

  /** A message is being sent to the lobby. */
  SEND_LOBBY_MESSAGE(2),

  /** Informs the client that they have successfully joined a lobby. */
  LOBBY_JOINED(1),

  /** A client has successfully exited the lobby. */
  LOBBY_EXITED(1),

  /** Send a list of all lobbies and the clients they contain */
  UPDATE_FULL_LIST(1),

  /** Send a list of all clients in the server. Used for the client list in the menu. */
  UPDATE_CLIENT_LIST(1),

  /** Sends the list of clients in the lobby. */
  UPDATE_LOBBY_LIST(1),

  /** Sets the ready status of a client to true or false. */
  TOGGLE_READY_STATUS(1),

  /** Informs the client that the game is starting. */
  START_GAME(0),

  /** Signal regularly sent from server to client to confirm connection. */
  SERVER_PING(0),

  /** Signal sent to client upon receiving a PING from the client. */
  SERVER_PONG(0),
  /** Signal sent to client upon receiving a pause request. */
  TOGGLE_PAUSE(0),
  /** Informs clients that the game is starting. */
  START_GAME_LOOP(0),
  /** Informs the client, that the jump request was successful. */
  JUMP(0),
  /** Updates the position of the cube for the client. */
  POSITION_UPDATE(2);

  private final int numArgs;

  /** Initialises the command */
  ServerProtocol(int numArgs) {
    this.numArgs = numArgs;
  }

  /**
   *Returns the number of arguments needed.
   * Called from {@link client.ServerIn} to separate the commands by their length
   * @return
   */
  public int getNumArgs() {
    return this.numArgs;
  }
}
