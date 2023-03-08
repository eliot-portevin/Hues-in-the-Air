package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.util.Date;
import java.io.PrintWriter;

public class Server {

    private static final int PORT = 9090;
    public static void main() throws IOException {
        ServerSocket listener = new ServerSocket(PORT);

        System.out.println("[SERVER] Waiting for client connection...");
        Socket client = listener.accept();
        System.out.println("[SERVER] Connected to Client!");
        PrintWriter out = new PrintWriter(client.getOutputStream(), true);
        out.println( new Date().toString() );
        System.out.println("[SERVER] Sent current date to client.");

        client.close();
        listener.close();
    }
}