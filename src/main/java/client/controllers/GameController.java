package client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class GameController {
  @FXML private Pane gamePane;

  public void initialize() {
    Label label = new Label("this is the game pane");
    gamePane.getChildren().add(label);
  }

  public Pane getPane() {
    return gamePane;
  }
}
