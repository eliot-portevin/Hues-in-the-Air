package server;

import client.ClientProtocol;

/**
 * Server protocol for Hues in the Air
 * <p>
 *     These are the commands that the server can send to the client.
 *     See {@link ClientProtocol} for the commands that the client can send to the server.
 * </p>
 * */
public enum ServerProtocol {
    /**
     * Separator for the protocol
     * <p>
     *     This is used to separate arguments in the protocol.
     * </p>
     * */
    SEPARATOR(0) { public String toString() { return "£"; } },
    /**
     * Request client username
     * */
    REQUEST_USERNAME(0) { public String toString() { return "REQUEST_USERNAME"; } },

    /**
     * Inform client that the username is already taken
     * */
    USERNAME_TAKEN(0),

    /**
     * Inform client that the username is valid
     * */
    USERNAME_VALID(0),

    /**
     * Send a chat message to a specific client
     * */
    SEND_MESSAGE_CLIENT(2),

    /**
     * Send a chat message to the whole server
     * */
    SEND_MESSAGE_SERVER(1),

    /**
     * Send a chat message to a specific lobby
     * */
    SEND_MESSAGE_LOBBY(2),

    /**
     * Client wants to change username
     * */
    CHANGE_USERNAME_REQUEST(1);

    private final int numArgs;

    ServerProtocol(int numArgs) {
        this.numArgs = numArgs;
    }
}