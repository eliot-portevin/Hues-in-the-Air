package client;

public class ClientMain {
    public static void main(String[] args) {
        String[] serverInfo = {"25.20.244.173:9090"};
        try {
            Client.run(serverInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
