package server;
/**
 * The main class of the server. It creates a new server and starts it.
 */
public class ServerMain {
    public static void main(String[] args) {
        String PORT = "9090";
        if (args.length == 1) { PORT = args[0]; }
        else { System.out.println("Using default port: " + PORT); }

        Server server = new Server(PORT);
        Thread serverThread = new Thread(server);
        serverThread.start();
    }
}
