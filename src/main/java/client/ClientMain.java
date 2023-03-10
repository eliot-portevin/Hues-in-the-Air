package client;

public class ClientMain {
    public static void main(String[] args) {
        String[] serverInfo = {"10.172.3.160:9090"};
        try {
            Client.run(serverInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
