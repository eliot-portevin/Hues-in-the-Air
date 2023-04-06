import client.ClientMain;
import server.ServerMain;

public class Main {

  public static void main(String[] args) {
    if (args.length == 2) {
      if (args[0].equals("client")) {
        String[] split = args[1].split(":");
        String hostAddress = split[0];
        String port = split[1];

        ClientMain.main(new String[] {hostAddress, port});
      } else if (args[0].equals("server")) {
        ServerMain.main(new String[] {args[1]});
      }
    }
    else if (args.length == 3) {
      if (args[0].equals("client")) {
        String[] split = args[1].split(":");
        String hostAddress = split[0];
        String port = split[1];
        String username = args[2];

        ClientMain.main(new String[] {hostAddress, port, username});
      }
    }
  }
}
