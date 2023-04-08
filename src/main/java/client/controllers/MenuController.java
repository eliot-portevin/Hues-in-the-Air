package client.controllers;

import client.Client;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.Arrays;

public class MenuController {
  // Sub-controllers
  @FXML private VBox homeTab;
  @FXML private MenuHomeController homeTabController;

  @FXML private GridPane backgroundPane;

  @FXML private ToggleButton tabGames;
  @FXML private ToggleButton tabHome;
  @FXML private ToggleButton tabSettings;

  @FXML private TextArea chat;
  @FXML private TextField textChat;

  @FXML
  public void initialize() {
    this.setButtonBehaviour();

    this.setFontBehaviour();

    this.setTabPaneBehaviour();

    this.initialiseChat();

  }

  private void initialiseChat() {
    this.textChat.setOnKeyPressed(
        e -> {
          if (e.getCode().toString().equals("ENTER")) {
            Client.getInstance().sendMessageServer(this.textChat.getText());
            this.textChat.clear();
          }
        });
  }

  /** Configures the tabs to play a click sound when the mouse enter them */
  private void setButtonBehaviour() {
    for (ToggleButton tab : Arrays.asList(tabHome, tabGames, tabSettings)) {
      tab.setOnMouseEntered(
          e -> {
            if (!tab.isSelected()) {
              Client.getInstance().clickSound();
            }
          });
    }
  }

  /** Binds the font size of the labels to the width of the window. */
  private void setFontBehaviour() {
    tabHome
        .styleProperty()
        .bind(Bindings.concat("-fx-font-size: ", backgroundPane.widthProperty().divide(50)));
    tabGames
        .styleProperty()
        .bind(Bindings.concat("-fx-font-size: ", backgroundPane.widthProperty().divide(50)));
    tabSettings
        .styleProperty()
        .bind(Bindings.concat("-fx-font-size: ", backgroundPane.widthProperty().divide(50)));
  }

  /**
   * Selects the home tab for the startup. Also sets the behaviour for the tabs, so that only one
   * tab can be selected at a time.
   */
  private void setTabPaneBehaviour() {
    for (ToggleButton tab : Arrays.asList(tabHome, tabGames, tabSettings)) {
      tab.setOnAction(
          e -> {
            for (ToggleButton otherTab : Arrays.asList(tabHome, tabGames, tabSettings)) {
              if (otherTab != tab) {
                configureTab(otherTab, false);
              }
            }
            configureTab(tab, true);
          });
    }
    configureTab(tabHome, true);
  }

  /**
   * Configures a tab, so that it is selected or not selected. Also sets the font size of the tab
   * depending on whether it is selected or not.
   *
   * @param tab The ToggleButton to configure
   * @param isSelected Whether the tab should be selected or not
   */
  private void configureTab(ToggleButton tab, boolean isSelected) {
    int fontSize = isSelected ? 45 : 50;
    tab.styleProperty()
        .bind(Bindings.concat("-fx-font-size: ", backgroundPane.widthProperty().divide(fontSize)));
    tab.setSelected(isSelected);
  }

  public void receiveMessage(String message, String sender) {
    this.chat.appendText(String.format("[%s]: %s%n", sender, message));
  }

  /**
   * Sets the lobby list in the home tab.
   *
   * @param lobbyInfo All the information about the lobbies
   */
  public void setLobbyList(String[][] lobbyInfo) {
    this.homeTabController.setLobbyList(lobbyInfo);
  }

  /**
   * Updates the list of users in the home tab.
   *
   * @param users A String array containing all the usernames
   */
  public void setUsersList(String[] users) {
    this.homeTabController.setUsersList(users);
  }

  /** Clear the text fields in the home tab. */
  public void clearHomeTab() {
    this.homeTabController.clear();
  }
}
