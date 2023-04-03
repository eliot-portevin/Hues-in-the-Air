import client.ClientMain;
import javafx.application.Application;import server.*;
// import client.*;

public class Main {

  public static void main(String[] args) {
    if (args.length == 2) {
      if (args[0].equals("client")) {
        String[] split = args[1].split(":");
        String hostAddress = split[0];
        String port = split[1];

        //ClientMain.main(new String[] {hostAddress, port});
        Application.launch(gui.GuiMain.class, args);
      } else if (args[0].equals("server")) {
        ServerMain.main(new String[] {args[1]});
      }
    }
  }
}
