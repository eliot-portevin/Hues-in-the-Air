package client.controllers;

import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class MenuGameController {

  @FXML private VBox gamesTab;

  @FXML private Label labelLobbyName;
  @FXML private Label labelScore;
  @FXML private Label labelStatus;

  @FXML private ListView<String> gameList;
  @FXML private ListView<String> gameStatusList;
  @FXML private ListView<String> scoreList;

  public void initialize() {
    this.initialiseLists();
  }

  /**
   * Initialises the list views in the games tab.
   */
  private void initialiseLists() {
    gameStatusList.setCellFactory(
        l ->
            new ListCell<>() {
              @Override
              protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                  setText(null);
                } else {
                  setText(item);
                  setAlignment(Pos.CENTER_RIGHT);
                }
              }
            });
    scoreList.setCellFactory(l -> new ListCell<>() {
      @Override
      protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
          setText(null);
        } else {
          setText(item);
          setAlignment(Pos.CENTER);
        }
      }
    });
  }

  /**
   * The client has received a full list of games from the server. This method updates the list of
   * games in the games tab.
   *
   * @param games The games to display
   */
  public void setGameList(String[] games) {
    this.gameList.getItems().clear();
    this.gameStatusList.getItems().clear();
    this.scoreList.getItems().clear();

    ArrayList<String> gameList = new ArrayList<>();
    ArrayList<String> gameStatusList = new ArrayList<>();
    ArrayList<String> scoreList = new ArrayList<>();

    for (String game : games) {
      String gameId = game.split(" ")[0];
      String gameStatus = game.split(" ")[1];

      gameList.add(gameId);
      gameStatusList.add(gameStatus.equals("true") ? "In Game" : "Finished");
      scoreList.add("Undefined");
    }

    this.gameList.getItems().addAll(gameList);
    this.gameStatusList.getItems().addAll(gameStatusList);
    this.scoreList.getItems().addAll(scoreList);
  }
}
