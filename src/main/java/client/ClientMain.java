package client;

import javafx.application.Application;

public class ClientMain {

    /**
     * Main method for the client. If the user has provided three arguments, these will directly be
     * passed to the text fields in the login screen. If the user has not provided any arguments, the
     * login screen will be shown with empty text fields.
     *
     * @param args The array of three String arguments provided by the user.
     */
    public static void main(String[] args) {
        String hostAddress = "";
        String port = "";
        String username = "";

        if (args.length == 2) {
            hostAddress = args[0];
            port = args[1];
        }
        else if (args.length == 3) {
            hostAddress = args[0];
            port = args[1];
            username = args[2];
        }
    }
}
