package client;

public class ClientMain {
    public static void main(String[] args) {
        // 25.20.244.173:9090
        String[] serverInfo = {"localhost:9090"};
        Client client = new Client();

        try {
            client.run(serverInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
