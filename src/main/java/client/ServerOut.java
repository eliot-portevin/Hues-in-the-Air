package client;

import static shared.Encryption.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;

/**
 * Sends commands from client to server.*/
public class ServerOut implements Runnable{

    private final Socket serverSocket;
    private final PrintWriter out;
    private final BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
    private final Client client;

    protected Boolean running = true;

    /**
     * Creates an instance of ServerOut*/
    public ServerOut(Socket serverSocket, Client client) throws IOException {
        this.serverSocket = serverSocket;
        this.out = new PrintWriter(this.serverSocket.getOutputStream(), true);
        this.client = client;
    }

    /**
     * From the Runnable interface. Runs the ServerOut thread to send commands to the server
     */
    @Override
    public void run() {
        try {
            while(this.running) {
                System.out.print("> ");
                String command = this.keyboard.readLine();

                if (command == null) {
                    System.out.println("[CLIENT] Keyboard: command is null");
                }
                else {
                    this.handleCommand(command);
                }
            }
        } catch (IOException e) {
            System.err.println("[CLIENT] ServerOut: " + e.getMessage());
            e.printStackTrace();
        }
        // Close the socket and the input stream
        try {
            this.serverSocket.close();
            this.keyboard.close();
        } catch (IOException e) {
            System.err.println("[CLIENT] Failed to close serverSocket and input stream: " + e.getMessage());
            e.printStackTrace();
        }

    }

    protected void sendToServer(String message) {
        this.out.println(encrypt(message));
    }

    private void handleCommand(String command) {
        String commandSymbol = ChatCommands.COMMAND_SYMBOL.toString();

        if (command.startsWith(commandSymbol)) {
            int firstSpace = command.indexOf(" ");

            try {
                ChatCommands chatCommand = ChatCommands.valueOf(command.substring(1, firstSpace).replace(commandSymbol, ""));
                String[] args = command.substring(firstSpace + 1).split(" ");

                switch (chatCommand) {
                    case exit : {
                        this.client.logout();
                    }
                    case broadcast: {
                        // do nothing
                    }
                    case say: {
                        // do nothing
                    }
                    case set_username: {
                        this.client.setUsername(args[0]);
                    }
                }
            } catch (IllegalArgumentException e) {
                System.out.println("[CLIENT] ServerOut: command " + command + " not recognized");
            }
        }
        else {
            System.out.println("[CLIENT] ServerOut: command does not start with command symbol");
        }
    }
}
