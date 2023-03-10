package server;

public class ServerMain {
    public static void main(String[] args) {
        Server server = new Server("9090");
        Thread serverThread = new Thread(server);
        serverThread.start();
    }
}
