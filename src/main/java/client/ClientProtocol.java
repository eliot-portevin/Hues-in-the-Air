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
  /** Set client username */
  SET_USERNAME(1),

  /** Send a chat message to lobby */
  SEND_LOBBY_MESSAGE(1),

  /** This client wants to send a private message to another client. */
  SEND_PRIVATE_MESSAGE(2),

  /** Send a chat message to the whole server */
  SEND_PUBLIC_MESSAGE(1),

  /** Client is exiting the program. */
  EXIT(0),

  /** Client wants to join a lobby */
  JOIN_LOBBY(2),

  /** Client wants to create a lobby */
  CREATE_LOBBY(2),
  /**
   * Client wants to exit the lobby
   */
  EXIT_LOBBY(0),

  /** Client wants to know the name of the other players */
  GET_CLIENTS_SERVER(0),

  /** Client wants to know the name of the other players in their lobby and their ready status. */
  GET_CLIENTS_LOBBY(0),

  /** Client wants a full list of all lobbies and players in the lobby. Called upon startup. */
  GET_FULL_SERVER_LIST(0),

  /** Client wants to toggle their ready status to true or false. */
  TOGGLE_READY_STATUS(1),

  CLIENT_PING(0),
  CLIENT_PONG(0),

  REQUEST_JUMP(0),

  REQUEST_PAUSE(0);

  private final int numArgs;

  /**
   * Initialises the ClientProtocol with the required number of arguments
   * @param numArgs
   */
  ClientProtocol(int numArgs) {
    this.numArgs = numArgs;
  }

  /**
   * Returns the number of arguments required
   * @return
   */
  public int getNumArgs() {
    return this.numArgs;
  }
}
