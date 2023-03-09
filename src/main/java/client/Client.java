package client;

import java.io.PrintWriter;
import java.net.Socket;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;


public class Client {

    private static final int SERVER_PORT = 9090;
    private static final String SERVER_IP = "127.0.0.1";
    private String username;

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket(SERVER_IP, SERVER_PORT);

        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        System.out.println("Client says: Connected to server!");

        while (true) {
            System.out.print("> ");
            String command = keyboard.readLine();

            if (command.equals("exit")) {
                out.println("exit");
                break;
            }
            out.println(command);

            String serverResponse = input.readLine();
            System.out.println(serverResponse);
        }

        socket.close();
        System.exit(0);
    }
}