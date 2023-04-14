package server;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Game implements Runnable {
  public static ArrayList<Color> blockColours =
      new ArrayList<>(
          Arrays.asList(
              Color.valueOf("#f57dc6"),
              Color.valueOf("#b3d5f2"),
              Color.valueOf("#9ae6ae"),
              Color.valueOf("#fccf78")));
  private final HashMap<ClientHandler, Color> clientColours;

  private Boolean running = true;

  public Game(HashMap<ClientHandler, Color> clientColours) {
    this.clientColours = clientColours;
  }

  private void start() {
    for (ClientHandler client : this.clientColours.keySet()) {
      client.startGame();
    }
  }

  /**
   * Runnable run method. This method is called when the thread is started.
   * */
  @Override
  public void run() {
    this.start();

    while (this.running) {
      this.update();
    }
  }

  /**
   * Updates the game. Called at every frame.
   */
  private void update() {
    this.running = false;
  }

}
