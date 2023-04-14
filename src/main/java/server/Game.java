package server;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Game {
  public static ArrayList<Color> blockColours = new ArrayList<>(Arrays.asList(
    Color.valueOf("#f57dc6"),
    Color.valueOf("#b3d5f2"),
    Color.valueOf("#9ae6ae"),
    Color.valueOf("#fccf78")
  ));
  private final HashMap<ClientHandler, Color> clientColours;

  public Game(HashMap<ClientHandler, Color> clientColours) {
    this.clientColours = clientColours;

    this.start();
  }

  private void start() {
    for (ClientHandler client : this.clientColours.keySet()) {
      client.startGame();
    }
  }
}
