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
  /** Client wants to exit the lobby */
  EXIT_LOBBY(0),
  /** Client wants a full list of all lobbies and players in the lobby. Called upon startup. */
  GET_FULL_SERVER_LIST(0),
  /** Client has opened the menu screen and wants to get the lists of clients/lobbies/games */
  GET_FULL_MENU_LISTS(0),
  /** The client is in the lobby and wishes to receive the list of clients in their lobby. */
  GET_FULL_LOBBY_LIST(0),
  /** Client wants to toggle their ready status to true or false. */
  TOGGLE_READY_STATUS(1),
  /** Signal regularly sent from client to server to confirm connection. */
  CLIENT_PING(0),
  /** Signal sent to server upon receiving a PING from the server. */
  CLIENT_PONG(0),
  /** Client wants to jump */
  SPACE_BAR_PRESSED(0),
  /** Client wants to pause the game */
  REQUEST_PAUSE(0),
  /** Client game is opened and is ready to start */
  READY_UP(0),
  /** Client has loaded the level successfully, wants to request the critical blocks */
  REQUEST_CRITICAL_BLOCKS(0),
  /** Client has pressed the quit game button, requests that the game be ended. */
  REQUEST_END_GAME(0),
  /** The clients in game want to skip the level and get to the next level. */
  SKIP_LEVEL(0),
  /** The clients in game want to become immortal. */
  SET_IMMORTAL(0),
  /** The clients in game want to be mortal again. */
  SET_MORTAL(0);

  private final int numArgs;

  /**
   * Initialises the ClientProtocol with the required number of arguments
   *
   * @param numArgs - The number of arguments required
   */
  ClientProtocol(int numArgs) {
    this.numArgs = numArgs;
  }

  /**
   * @return the number of arguments required
   */
  public int getNumArgs() {
    return this.numArgs;
  }
}
