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
     * */
    SET_USERNAME(1),

    /**
     * Send a chat message to lobby
     * */
    SEND_MESSAGE_LOBBY(1),

    /**
     * Send a chat message to another client
     * */
    SEND_MESSAGE_CLIENT(2),

    /**
     * Send a chat message to the whole server
     * */
    SEND_MESSAGE_SERVER(1);

    private final int numArgs;

    ClientProtocol(int numArgs) {
        this.numArgs = numArgs;
    }
}
