package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Sends commands from client to server.*/
public class ServerOut implements Runnable{

    private final Socket serverSocket;
    private final PrintWriter out;
    private final BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));

    /**
     * Creates an instance of ServerOut*/
    public ServerOut(Socket serverSocket) throws IOException {
        this.serverSocket = serverSocket;
        this.out = new PrintWriter(this.serverSocket.getOutputStream(), true);
    }

    /**
     * From the Runnable interface. Runs the ServerOut thread to send commands to the server
     */
    @Override
    public void run() {
        try {
            while(true) {
                System.out.print("> ");
                String command = this.keyboard.readLine();

                if (command.equals("quit")) {
                    this.out.println(command);
                    this.serverSocket.close();
                    break;
                }
                else {
                    this.out.println(command);
                }
            }
        } catch (IOException e) {
            System.err.println("[CLIENT] ServerOut: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
