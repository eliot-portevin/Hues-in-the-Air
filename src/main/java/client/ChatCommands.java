package client;

public enum ChatCommands {
    /**
     * Symbol used to detect a command.
     * */
    COMMAND_SYMBOL(0) { public String toString() { return "!"; } },

    /**
     * Client wants to exit the program.
     * */
    exit(0),

    /**
     * Client wants to set their username.
     * */
    set_username(1),

    /**
     * Client wants to send private message to another client.
     * */
    say(2),

    /**
     * Client wants to send a message to whole server.
     * <p>
     *     Protocol format: <COMMAND_SYMBOL>broadcast<SEPARATOR>message
     * </p>
     * */
    broadcast(1);

    /**
     * Number of arguments that the command takes.
     * */
    private final int numArgs;

    ChatCommands(int numArgs) {
        this.numArgs = numArgs;
    }

    public int getNumArgs() {
        return numArgs;
    }
}
