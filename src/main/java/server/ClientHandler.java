package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;

public class ClientHandler implements Runnable {

    private final Socket client;
    private final BufferedReader in;
    private final PrintWriter out;
    private final Server server;

    public ClientHandler(Socket clientSocket, Server server) throws IOException {
        this.client = clientSocket;
        this.in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        this.out = new PrintWriter(client.getOutputStream(), true);
        this.server = server;
    }
    /**
     * When an object implementing interface {@code Runnable} is used
     * to create a thread, starting the thread causes the object's
     * {@code run} method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method {@code run} is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        try {
            while (true) {
                String request = in.readLine();
                if (request.equals("exit")) {
                    this.server.shutdown();
                    break;
                }
                else if (request.startsWith("say")) {
                    int firstSpace = request.indexOf(" ");
                    if (firstSpace != -1) {
                        String message = request.substring(firstSpace + 1);
                        this.broadcast(message);
                    }
                }
            }
        } catch(IOException e) {
            System.err.println("IO exception in client handler");
            System.err.println(Arrays.toString(e.getStackTrace()));
        } finally {
            try {
                client.close();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void broadcast(String message) {
        for (ClientHandler client : this.server.getClients()) {
            client.out.println(message);
        }
    }
}