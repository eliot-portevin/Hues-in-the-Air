package server;

import client.ClientProtocol;
import com.sun.javafx.fxml.ParseTraceElement;

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
    SEPARATOR(0) {
        public String toString() { return "¶"; } },
    /**
     * Request client username
     * */
    NO_USERNAME_SET(0),

    /**
     * Inform client that the username is already taken
     * */
    USERNAME_TAKEN(0),

    /**
     * Inform client that the username is valid
     * */
    USERNAME_VALID(0),

    /**
     * No user with that username was found. Called from {@link ClientHandler}.
     * */
    NO_USER_FOUND(0) { public String toString() { return "NO_USER_FOUND"; } },

    /**
     * A message is being sent to another client.
     * <p>
     *     Format: SEND_MESSAGE_CLIENT<Separator>sender.username<Separator>receiver.username<Separator>message
     * </p>
     * */
    SEND_MESSAGE_CLIENT(3),

    /**
     * A message is being sent to the whole server.
     * <p>
     *     Format: SEND_MESSAGE_SERVER<Separator>username<Separator>message
     * </p>
     * */
    SEND_MESSAGE_SERVER(2),

    /**
     * A message is being sent to the lobby.
     * */
    SEND_MESSAGE_LOBBY(2),

    PONG(0);

    private final int numArgs;

    ServerProtocol(int numArgs) {
        this.numArgs = numArgs;
    }

    public int getNumArgs() {
        return this.numArgs;
    }
}
