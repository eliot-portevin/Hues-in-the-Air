package server;

public class ServerSocket {

    int portNumber;
    public ServerSocket(int portNumber) {
        this.portNumber = portNumber;
        System.out.println("ServerSocket created");
    }

    public Socket accept() {
        System.out.println("ServerSocket accepted");
        return new Socket();
    }
}
