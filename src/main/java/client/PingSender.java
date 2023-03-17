package client;


public class PingSender implements Runnable{

    Client client;
    Boolean running = true;

    public PingSender(Client client) {
        this.client = client;
    }

    public void run() {
        while (this.running) {
            try{
                Thread.sleep(1000);
                if(client.connectedToServer) {
                    client.connectedToServer = false;
                    client.ping();
                }
                else {
                    if(client.noAnswerCounter > 3) {
                        client.reconnect();
                    }
                    else {
                        client.noAnswerCounter++;
                        client.connectedToServer = true;
                    }

                }
            } catch (InterruptedException e) {
                System.out.println("Ping sender couldn't sleep.");
                throw new RuntimeException(e);
            }
        }

    }
}
