package client;

import server.ServerProtocol;

/**
 * The Client protocol for Hues in the Air
 * <p>
 *     These are the commands that the client can send to the server.
 *     See {@link ServerProtocol} for the commands that the server can send to the client.
 * </p>
 *
 * */
public enum ClientProtocol {
    /**
     * Set client username
     * <p>
     *     Format: SET_USERNAME<Separator>username
     * </p>
     * */
    SET_USERNAME(1),

    /**
     * Send a chat message to lobby
     * <p>
     *     Format: SEND_MESSAGE_LOBBY<Separator>message
     * </p>
     * */
    SEND_MESSAGE_LOBBY(1),

    /**
     * This client wants to send a private message to another client.
     * <p>
     *     Protocol format: SEND_MESSAGE_CLIENT<SEPARATOR>receiver.username<SEPARATOR>message
     * </p>
     * */
    WHISPER(2),

    /**
     * Send a chat message to the whole server
     * <p>
     *     Format: SEND_MESSAGE_SERVER<Separator>username<Separator>message
     * </p>
     * */
    BROADCAST(1),

    /**
     * Client is exitting the program.
     * */
    LOGOUT(0),

    /**
     * Client wants to join a lobby
     * <p>
     *     Format: JOIN_LOBBY<Separator>lobbyName<Separator>password
     * </p>
     */
    JOIN_LOBBY(2),

    /**
     * Client wants to create a lobby
     * <p>
     *     Format: CREATE_LOBBY<Separator>lobbyName<Separator>password
     * </p>
     */
    CREATE_LOBBY(2),

    PING(0);

    private final int numArgs;

    ClientProtocol(int numArgs) {
        this.numArgs = numArgs;
    }

    public int getNumArgs() {
        return this.numArgs;
    }
}
