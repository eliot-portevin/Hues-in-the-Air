package client;

public class ClientMain {
    public static void main(String[] args) {
        String[] serverInfo = {"10.192.5.243:9090"};
        try {
            Client.run(serverInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
