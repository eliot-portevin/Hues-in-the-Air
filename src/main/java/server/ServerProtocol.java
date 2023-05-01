package server;

import client.ClientProtocol;

/**
 * Server protocol for Hues in the Air
 *
 * <p>These are the commands that the server can send to the client.
 * See {@link ClientProtocol} for the commands
 * that the client can send to the server.
 */
public enum ServerProtocol {
  /**
   * Separator for the protocol
   *
   * <p>This is used to separate arguments in the protocol.
   */
  SEPARATOR(0) {
    /** Separator for the protocol.
     * @return <&!> the separator */
    public String toString() {
      return "<&!>";
    }
  },
  /**
   * Separator used to separate subarguments.
   */
  SUBSEPARATOR(0) {
    /** The separator of the subarguments.
     * @return <&.> the separator of the subarguments
     */
    public String toString() {
      return "<&.>";
    }
  },

  /** Separator used to separate subsubarguments. */
  SUBSUBSEPARATOR(0) {
    /** The separator of the subsubarguments.
     *@return <&..> the separator of the subsubarguments */
    public String toString() {
      return "<&..>";
    }
  },

  /** Inform client that the username is already taken. */
  USERNAME_SET_TO(1),

  /** No user with that username was found.
   *  Called from {@link ClientHandler}. */
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

  /** Send a list of all lobbies and the clients they contain. */
  UPDATE_FULL_LIST(1),

  /** Send a list of all clients in the server.
   * Used for the client list in the menu. */
  UPDATE_CLIENT_LIST(1),

  /** Sends the list of clients in the lobby. */
  UPDATE_LOBBY_LIST(1),

  /** Sends the list of games that have been played or are being played. */
  UPDATE_GAME_LIST(1),

  /** Sets the ready status of a client to true or false. */
  TOGGLE_READY_STATUS(1),

  /** Informs the client that the game is starting. */
  START_GAME(0),

  /** Signal regularly sent from server to client to confirm connection. */
  SERVER_PING(0),

  /** Signal sent to client upon receiving a PING from the client. */
  SERVER_PONG(0),
  /** The game has been closed.
   *  Inform the clients that they can go back to their lobby screen. */
  GAME_ENDED(0),
  /** Sends the critical blocks and their colour to the client. */
  SEND_CRITICAL_BLOCKS(1),
  /** Updates the position of the cube for the client. */
  POSITION_UPDATE(5),
  /** The cube has just jumped. Informs the client
   *  of the coordinates of the rotation point. */
  JUMP_UPDATE(2),
  /**
   * Informs the players in the game of how many lives they have left
   * and how many levels they have completed.
   */
  GAME_STATUS_UPDATE(2),
  /** Tells the client to load new level. */
  LOAD_LEVEL(1);
  /** The number of arguments. */
  private final int numArgs;

  /** Initialises the command.
   * @param numbArgs the number of arguments
   */
  ServerProtocol(final int numbArgs) {
    this.numArgs = numbArgs;
  }

  /**
   * Returns the number of arguments needed.
   * Called from {@link client.ServerIn} to separate the
   * commands by their length
   *
   * @return The number of arguments needed to send the command.
   */
  public int getNumArgs() {
    return this.numArgs;
  }
}
