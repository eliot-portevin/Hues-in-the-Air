package server;

public class ClientHandler {

    public ClientHandler(Socket clientSocket, Main main) {
        System.out.println("ClientHandler created");
    }

    public void start() {
        System.out.println("ClientHandler started");
    }

}
