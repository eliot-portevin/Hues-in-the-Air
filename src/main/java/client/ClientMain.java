package client;

public class ClientMain {
    public static void main(String[] args) {
        String[] serverInfo = {"131.152.231.246:9090"};
        try {
            Client.run(serverInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
