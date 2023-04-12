package client.controllers;

import client.Game;
import client.GameMain;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class GameController {
  @FXML private Pane gamePane;

  private Game game;

  public void initialize() {
    this.game = new Game();
    game.run(gamePane);
  }

  public Pane getPane() {
    return gamePane;
  }
}
