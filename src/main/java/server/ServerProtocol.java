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

  /**
   * Separator used for lobby lists
   */
  LOBBY_INFO_SEPARATOR(0) {
    public String toString() {
      return "<&?>";
    }
  },

  /** Request client username */
  NO_USERNAME_SET(0),

  /** Inform client that the username is already taken */
  USERNAME_SET_TO(1),

  /** No user with that username was found. Called from {@link ClientHandler}. */
  NO_USER_FOUND(1),

  /**
   * A message is being sent to another client.
   *
   * <p>Format: WHISPER<Separator>sender.username<Separator>message
   */
  WHISPER(2),

  /**
   * A message is being sent to the whole server.
   *
   * <p>Format: SEND_MESSAGE_SERVER<Separator>username<Separator>message
   */
  BROADCAST(2),

  /** A message is being sent to the lobby. */
  SEND_MESSAGE_LOBBY(2),

  SEND_CLIENT_LIST(1),

  /** Informs the client that they have successfully joined a lobby. */
  LOBBY_JOINED(1),

  /* A client has successfully exited the lobby.*/
  LOBBY_EXITED(1),

  /* Send a list of all lobbies and the clients they contain */
  UPDATE_LOBBY_LIST(1),

  /** Send a list of all clients in the server. Used for the client list in the menu. */
  UPDATE_CLIENT_LIST(1),

  SERVER_PING(0),

  SERVER_PONG(0);

  private final int numArgs;

  ServerProtocol(int numArgs) {
    this.numArgs = numArgs;
  }

  public int getNumArgs() {
    return this.numArgs;
  }
}
