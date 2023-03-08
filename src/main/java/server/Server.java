package server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.util.Date;
import java.io.PrintWriter;

public class Server {

    private static final int PORT = 9090;
    private static String[] names = {"John", "Paul", "George", "Ringo"};
    private static String[] adjectives = {"the smart", "the funny", "the handsome", "the ugly"};

    public static void main(String[] args) throws IOException {
        ServerSocket listener = new ServerSocket(PORT);

        System.out.println("[SERVER] Waiting for client connection...");
        Socket client = listener.accept();
        System.out.println("[SERVER] Connected to Client!");

        PrintWriter out = new PrintWriter(client.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));

        try {
            while (true) {
                String request = in.readLine();
                if (request == "exit") break;
                else if (request.contains("name")) out.println(getRandomName());
                else out.println("Type 'tell me a name' to get a random name.");
            }
        } finally {
            client.close();
            listener.close();
        }
    }

    public static String getRandomName() {
        String name = names[(int) (Math.random() * names.length)];
        String adjective = adjectives[(int) (Math.random() * adjectives.length)];

        return name + " " + adjective;
    }
}