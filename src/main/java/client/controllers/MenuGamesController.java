package client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.Arrays;

public class MenuGamesController {

  @FXML private VBox gamesTab;

  @FXML private Label labelLobbyName;
  @FXML private Label labelScore;
  @FXML private Label labelStatus;

  @FXML private ListView<String> list;

  public void initialize() {
    list.getItems().add("Hello");
    list.getItems().add("World");
  }
}
