package client.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

/** The controller for the games tab in the menu window. */
public class MenuGameController {

  @FXML private VBox highscoreTab;

  @FXML private Label labelLobbyName;
  @FXML private Label labelScore;
  @FXML private Label labelStatus;

  @FXML private ListView<String> gameList;
  @FXML private ListView<String> gameStatusList;
  @FXML private ListView<String> scoreList;

  /**
   * Initializes the controller class. This method is automatically called after the fxml file has
   * been loaded.
   */
  public void initialize() {
    this.initialiseLists();
    this.initialiseFonts();
  }

  /** Initialises the list views in the games tab. */
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
    scoreList.setCellFactory(
        l ->
            new ListCell<>() {
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

  private void initialiseFonts() {
    labelLobbyName
        .styleProperty()
        .bind(Bindings.concat("-fx-font-size: ", highscoreTab.widthProperty().divide(25)));
    labelScore
        .styleProperty()
        .bind(Bindings.concat("-fx-font-size: ", highscoreTab.widthProperty().divide(25)));
    labelStatus
        .styleProperty()
        .bind(Bindings.concat("-fx-font-size: ", highscoreTab.widthProperty().divide(25)));

    gameList
        .styleProperty()
        .bind(Bindings.concat("-fx-font-size: ", highscoreTab.widthProperty().divide(35)));
    gameStatusList
        .styleProperty()
        .bind(Bindings.concat("-fx-font-size: ", highscoreTab.widthProperty().divide(35)));
    scoreList
        .styleProperty()
        .bind(Bindings.concat("-fx-font-size: ", highscoreTab.widthProperty().divide(35)));
  }

  /**
   * The client has received a full list of games from the server. This method updates the list of
   * games in the games tab.
   *
   * @param games The games to display
   */
  public void setGameList(String[] games) {
    Arrays.sort(games, new Comparator<String>() {
      public int compare(String str1, String str2) {
        try {
          int int1 = Integer.parseInt(str1.split(" ")[1]);
          int int2 = Integer.parseInt(str2.split(" ")[1]);

          return Integer.valueOf(int2).compareTo(Integer.valueOf(int1));
        } catch (Exception e) {
          return 0;
        }
      }
    });

    this.gameList.getItems().clear();
    this.gameStatusList.getItems().clear();
    this.scoreList.getItems().clear();

    ArrayList<String> gameList = new ArrayList<>();
    ArrayList<String> gameStatusList = new ArrayList<>();
    ArrayList<String> scoreList = new ArrayList<>();

    try {
      for (String game : games) {
        String gameId = game.split(" ")[0];
        String gameScore = game.split(" ")[1];
        String gameStatus = game.split(" ")[2];

        gameList.add(gameId);
        gameStatusList.add(gameStatus.equals("true") ? "In Game" : "Finished");
        scoreList.add(gameScore);
      }

      this.gameList.getItems().addAll(gameList);
      this.gameStatusList.getItems().addAll(gameStatusList);
      this.scoreList.getItems().addAll(scoreList);
    } catch (Exception e) {
      this.gameList.getItems().add("Received invalid game list from server!");
    }
  }
}
