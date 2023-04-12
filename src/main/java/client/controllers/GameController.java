package client.controllers;

import client.Game;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

public class GameController {
  @FXML private Pane gamePane;

  private Game game;

  public void initialize() {
    this.game = new Game();
    game.run(this.gamePane);
  }

  private void initialiseKeyboard() {
    this.gamePane.setOnKeyPressed(e -> {
      if (e.getCode().toString().equals("SPACE")) {
        this.game.jump();
      }
    });
  }
}
