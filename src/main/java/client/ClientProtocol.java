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
     *     Format: SET_USERNAME¶username
     * </p>
     * */
    SET_USERNAME(1),

    /**
     * Send a chat message to lobby
     * <p>
     *     Format: SEND_MESSAGE_LOBBY¶username¶message
     * </p>
     * */
    SEND_MESSAGE_LOBBY(1),

    /**
     * Send a chat message to another client
     * <p>
     *     Format: SEND_MESSAGE_CLIENT¶username¶message
     * </p>
     * */
    SEND_MESSAGE_CLIENT(2),

    /**
     * Send a chat message to the whole server
     * <p>
     *     Format: SEND_MESSAGE_SERVER¶username¶message
     * </p>
     * */
    SEND_MESSAGE_SERVER(1),

    /**
     * Client logs out
     * */
    LOGOUT(0);

    private final int numArgs;

    ClientProtocol(int numArgs) {
        this.numArgs = numArgs;
    }
}
