package client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Arrays;

public class MenuGameController {

  @FXML private VBox gamesTab;

  @FXML private Label labelLobbyName;
  @FXML private Label labelScore;
  @FXML private Label labelStatus;

  @FXML private ListView<String> list;

  public void initialize() {
    this.setGameList("Lobby 1 Won Finished<&?>Lobby 2 - Lost InGame");
  }

  /**
   * The client has received a full list of games from the server. This method updates the list of
   * games in the games tab.
   * @param command The command received from the server
   */
  public void setGameList(String command) {
    this.list.getItems().clear();

    String[] games = command.split("<&\\?>");
    ArrayList<String> gameList = new ArrayList<>(Arrays.asList(games));

    this.list.getItems().addAll(gameList);
  }
}
