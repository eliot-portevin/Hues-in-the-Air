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
     *     Format: SEND_MESSAGE_LOBBY<Separator>username<Separator>message
     * </p>
     * */
    SEND_MESSAGE_LOBBY(1),

    /**
     * This client wants to send a private message to another client.
     * <p>
     *     Protocol format: SEND_MESSAGE_CLIENT<SEPARATOR>receiver.username<SEPARATOR>message
     * </p>
     * */
    SEND_MESSAGE_CLIENT(2),

    /**
     * Send a chat message to the whole server
     * <p>
     *     Format: SEND_MESSAGE_SERVER<Separator>username<Separator>message
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
