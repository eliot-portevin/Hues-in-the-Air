import server.*;
import client.*;

public class Main {

    public static void main(String[] args) {
        Server server = new Server();
        Client client = new Client();

        try {
            server.main();
            client.main();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}